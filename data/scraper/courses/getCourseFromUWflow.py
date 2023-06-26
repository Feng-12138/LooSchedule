import requests
import re
from dotenv import dotenv_values, load_dotenv
from sqlite3 import OperationalError
from multiprocessing.dummy import Pool
from __init__ import Session
from schemas import Course, Prerequisite
import os

load_dotenv()

yearList = []
courseDict = {}
levels = ['1A', '1B', '2A', '2B', '3A', "3B", "4A", "4B"]

coursePrereqDict = {}

config = dotenv_values(".env")

def getUrl(url: str, query = ""):
    data = ""
    try:
        if query == "":
            res = requests.get(url=url)
            data = res.text
        else:
            res = requests.post(url=url, json={'query': query})
            data = res.json()
    except ValueError:
        print("error when obtaining course urls")
    return data

def parseCourseUrl(data: str, yearList: list):
    ex = '<a href="/courses/[a-zA-Z]*">'
    urlList = []
    lst = re.findall(ex, data)
    for year in yearList:
        for item in lst:
            url = item.split('<a href="/courses/')[-1].split('">')[0]
            courseStr = f"course-{url}.html"
            url = os.environ["CourseCalendarBaseUrl"] + year + "/COURSE/" + courseStr
            urlList.append(url)
    return urlList

def getAllCoursePage(urlList: list):
    pool = Pool(6)
    print(len(urlList))
    retval = pool.map(getUrl, urlList)
    print(len(retval))
    return retval

def parseCourses(coursePages: list):
    courseInfoStrList = []
    for coursePage in coursePages:
        ex = "<center>.*</center>"
        courseInfo = re.findall(ex, coursePage)
        courseInfoStrList.append(courseInfo)
        
        
        
def wrapperCourseDataFunc(curYear: int):
    enterYear = curYear - 6
    for year in range(enterYear, curYear + 1):
        startYear = str(year)[-2:]
        endYear = str(year + 1)[-2:]
        yearList.append(startYear + endYear)
    data = getUrl(os.environ["CourseIndexUrl"])
    urlList = parseCourseUrl(data, yearList)
    coursePages = getAllCoursePage(urlList)
    # get all the courses first
    courseInfoStrList = []
    for coursePage in coursePages:
        ex = "<center>.*</center>"
        courseInfo = re.findall(ex, coursePage)
        courseInfoStrList.append(courseInfo)
    for courseInfoLst in courseInfoStrList:
        for courseInfo in courseInfoLst:
            exNumName = "<strong>.*</strong>"
            NumName = re.findall(exNumName, courseInfo)
            NumNameList = []
            infoList = []
            courseNum = ""
            credit = ""
            if (courseInfo.find('<img src=') != -1):
                continue
            if len(NumName) == 0:
                exNumName = "<td align=left><B><a name =.*</b></td><td align=right>"
                NumName = re.findall(exNumName, courseInfo)
                infoList = NumName[0].split('</a>')[-1].split('</b></td><td align=right>')[0].split(" ")
                tempStr = (infoList[0] + " " + infoList[1])
                courseNum = tempStr.upper()
                credit = infoList[-1].strip()
            else:
                NumNameList = NumName[0].split('</strong>')
                infoList = NumNameList[0].split('</a>')[-1].split(' ')
                tempStr = (infoList[0] + " " + infoList[1])
                courseNum = tempStr.upper()
                credit = infoList[-1].strip()
            credit = float(credit)
            
            if courseDict.get(courseNum) == None:
                courseDict[courseNum] = Course()
                courseDict[courseNum].courseID = courseNum.upper()
                courseDict[courseNum].subject = infoList[0]
                courseDict[courseNum].code = infoList[1]
                courseDict[courseNum].credit = credit
                courseDict[courseNum].courseName = ""
                courseDict[courseNum].description = ""
                courseDict[courseNum].easyRating = ""
                courseDict[courseNum].likedRating = ""
                courseDict[courseNum].usefulRating = ""
                courseDict[courseNum].antireqs = ""
                courseDict[courseNum].coreqs = ""
                courseDict[courseNum].antireqs = ""
    uwFlowCourseList = getUrl(os.environ['uwflowUrl'], query="""query Course {
    course {
        antireqs
        code
        coreqs
        description
        id
        name
        prereqs
        rating {
            easy
            liked
            useful
        }
        sections {
            term_id
            meetings {
                location
            }
        }
    }
}
""")["data"]["course"]
    for course in uwFlowCourseList:
        temp = course["code"]
        courseNum = temp.upper()
        splitIdx = 0
        for idx, char in enumerate(courseNum):
            if char >= "0" and char <= "9":
                splitIdx = idx
                break
        courseNum = courseNum[:splitIdx] + " " + courseNum[splitIdx:]
        if courseDict.get(courseNum) != None:
            courseDict[courseNum].courseName = course["name"]
            courseDict[courseNum].description = course["description"]
            courseDict[courseNum].easyRating = course["rating"]["easy"]
            courseDict[courseNum].likedRating = course["rating"]["liked"]
            courseDict[courseNum].usefulRating = course['rating']['useful']
            courseDict[courseNum].antireqs = course['antireqs']
            courseDict[courseNum].coreqs = course['coreqs']
            courseDict[courseNum].antireqs = course['antireqs']
            # courseDict[courseNum].prereqs = course['prereqs']
            if len(course['sections']) == 0:
                courseDict[courseNum].availability = ""
                courseDict[courseNum].onlineTerms = ""
            else:
                availableStr = ""
                onlineStr = ""
                for section in course['sections']:
                    termId = str(section["term_id"])
                    if termId[-1] == "5" and availableStr.find("S") == -1:
                        availableStr += "S"
                        if (len(section["meetings"]) > 0):
                            for obj in section["meetings"]:
                                if obj['location'].find("ONLN") != -1 and onlineStr.find("S") == -1:
                                    onlineStr += "S"
                    elif termId[-1] == "1" and availableStr.find("W") == -1:
                        availableStr += "W"
                        if (len(section["meetings"]) > 0):
                            for obj in section["meetings"]:
                                if obj['location'].find("ONLN") != -1 and onlineStr.find("W") == -1:
                                    onlineStr += "W"
                    elif termId[-1] == "9" and availableStr.find("F") == -1:
                        availableStr += "F"
                        if (len(section["meetings"]) > 0):
                            for obj in section["meetings"]:
                                if obj['location'].find("ONLN") != -1 and onlineStr.find("F") == -1:
                                    onlineStr += "F"
                courseDict[courseNum].availability = availableStr
                courseDict[courseNum].onlineTerms = onlineStr
            coursePrereqDict[courseNum] = course['prereqs']
        else:
            pass
    keyList = []
    for key in courseDict.keys():
        if (courseDict[key].courseName == ""):
            keyList.append(key)
    for key in keyList:
        courseDict.pop(key)
    return courseDict.values()
    
# def wrapperCourseDataFunc(curYear: int):
#     enterYear = curYear - 6
#     for year in range(enterYear, curYear + 1):
#         startYear = str(year)[-2:]
#         endYear = str(year + 1)[-2:]
#         yearList.append(startYear + endYear)
#     data = getUrl(os.environ["CourseIndexUrl"])
#     urlList = parseCourseUrl(data, yearList)
#     coursePages = getAllCoursePage(urlList)
#     # get all the courses first
#     courseInfoStrList = []
#     for coursePage in coursePages:
#         # if (coursePage.find("PD 8") != -1):
#         #     print("here5")
#         ex = "<center>.*</center>"
#         # courseInfo = re.findall(ex, coursePage)
#         courseInfo = coursePage.split("<center>")
#         # if (coursePage.find("PD 9") != -1):
#         courseInfoStrList.append(courseInfo)
#     for courseInfoLst in courseInfoStrList:
#         for courseInfo in courseInfoLst:
#             exNumName = "<strong>.*</strong>"
#             # NumName = re.findall(exNumName, courseInfo)
#             NumName = courseInfo.split('<strong>')
#             # if len(NumName) == 0:
#             #     continue
#             NumNameList = []
#             infoList = []
#             courseNum = ""
#             credit = ""
#             if (courseInfo.find('<img src=') != -1):
#                 continue
#             if len(NumName) == 0:
#                 exNumName = "<td align=left><B><a name =.*</b></td><td align=right>"
#                 # NumName = re.findall(exNumName, courseInfo)
#                 NumName = courseInfo.split("<td align=left><B><a name =")
#                 print(NumName, "here454")
#                 infoList = NumName[0].split('</a>')[-1].split('</b></td><td align=right>')[0].split(" ")
#                 courseNum = (infoList[0] + ' ' + infoList[1]).upper()
#                 credit = infoList[-1].strip()
#             else:
#                 NumNameList = NumName[0].split('</strong>')
#                 print(NumNameList)
#                 infoList = NumNameList[0].split('</a>')[-1].split(' ')
#                 courseNum = (infoList[0] + ' ' + infoList[1]).upper()
#                 credit = infoList[-1].strip()
#             credit = float(credit)
#             if courseNum == "PD 9":
#                 print("here1")
#             if courseDict.get(courseNum) == None:
#                 courseDict[courseNum] = Course()
#                 courseDict[courseNum].courseID = courseNum
#                 courseDict[courseNum].subject = infoList[0]
#                 courseDict[courseNum].code = infoList[1]
#                 courseDict[courseNum].credit = credit
#     uwFlowCourseList = getUrl(os.environ['uwflowUrl'], query="""query Course {
#     course {
#         antireqs
#         code
#         coreqs
#         description
#         id
#         name
#         prereqs
#         rating {
#             easy
#             liked
#             useful
#         }
#         sections {
#             term_id
#             meetings {
#                 location
#             }
#         }
#     }
# }
# """)["data"]["course"]
#     for course in uwFlowCourseList:
#         courseNum = course["code"]
#         separateIdx = 0
#         for idx, char in enumerate(courseNum):
#             if char <= "9" and char >= "0":
#                 separateIdx = idx
#                 break
#         courseNum = courseNum[:separateIdx].upper() + " " + courseNum[separateIdx:]
#         if courseDict.get(courseNum) != None:
#             courseDict[courseNum].courseName = course["name"]
#             courseDict[courseNum].description = course["description"]
#             courseDict[courseNum].easyRating = course["rating"]["easy"]
#             courseDict[courseNum].likedRating = course["rating"]["liked"]
#             courseDict[courseNum].usefulRating = course['rating']['useful']
#             courseDict[courseNum].antireqs = course['antireqs']
#             courseDict[courseNum].coreqs = course['coreqs']
#             courseDict[courseNum].antireqs = course['antireqs']
#             # courseDict[courseNum].prereqs = course['prereqs']
#             if len(course['sections']) == 0:
#                 courseDict[courseNum].availability = ""
#                 courseDict[courseNum].onlineTerms = ""
#             else:
#                 availableStr = ""
#                 onlineStr = ""
#                 for section in course['sections']:
#                     termId = str(section["term_id"])
#                     if termId[-1] == "5" and availableStr.find("S") == -1:
#                         availableStr += "S"
#                         if (len(section["meetings"]) > 0):
#                             for obj in section["meetings"]:
#                                 if obj['location'].find("ONLN") != -1 and onlineStr.find("S") == -1:
#                                     onlineStr += "S"
#                     elif termId[-1] == "1" and availableStr.find("W") == -1:
#                         availableStr += "W"
#                         if (len(section["meetings"]) > 0):
#                             for obj in section["meetings"]:
#                                 if obj['location'].find("ONLN") != -1 and onlineStr.find("W") == -1:
#                                     onlineStr += "W"
#                     elif termId[-1] == "9" and availableStr.find("F") == -1:
#                         availableStr += "F"
#                         if (len(section["meetings"]) > 0):
#                             for obj in section["meetings"]:
#                                 if obj['location'].find("ONLN") != -1 and onlineStr.find("F") == -1:
#                                     onlineStr += "F"
#                 courseDict[courseNum].availability = availableStr
#                 courseDict[courseNum].onlineTerms = onlineStr
#             coursePrereqDict[courseNum] = course['prereqs']
#             # coursePrereq = Prerequisite()
#             # coursePrereq.courseID = courseNum
#             # coursePrereq.consentRequired = False
#             # if courseDict[courseNum].prereqs.find("Level at least") != -1:
#             #     coursePrereq.minimumLevel = courseDict[courseNum].prereqs.split("Level at least")[-1].strip()[:2]   
#         else:
#             if courseNum.find("CS") != -1:
#                 print(courseNum) 
#             pass
#     return courseDict.values()


def handleOneOf(prereq: str) -> str:
    oneOfStr = ""
    if (prereq):
        oneOfIdxArr = []
        twoOfIdxArr = []
        threeOfIdxArr = []
        closeBracketIdxArr = []
        semiIdxArr = []
        for i in re.finditer("One of|one of", prereq):
            oneOfIdxArr.append(i.start())
                
        for i in re.finditer("Three of|three of", prereq):
            threeOfIdxArr.append(i.start())
                
        for i in re.finditer("Two of|two of", prereq):
            twoOfIdxArr.append(i.start())
            
        for i in re.finditer(";", prereq):
            semiIdxArr.append(i.start())
            
        for i in re.finditer("\)", prereq):
            closeBracketIdxArr.append(i.start())
                
        for oneIdx in oneOfIdxArr:
            closeSemiIdx = 99999
            closebracketIdx = 99999
            for semiIdx in semiIdxArr:
                if semiIdx > oneIdx:
                    closeSemiIdx = semiIdx
                    break
            for closeBracket in closeBracketIdxArr:
                if closeBracket > oneIdx:
                    closebracketIdx = closeBracket
                    break
            betterIdx = min(closeSemiIdx, closebracketIdx)
            
            curStr = prereq[oneIdx:betterIdx]             
            curStr = curStr.replace("One of", "1:")
            curStr = curStr.replace("one of", "1:")
            curStr = curStr.replace(" ", '')
            curStr = curStr.replace("/", ",")
            curStr = curStr.replace("or", ",")
            curStr = curStr.replace(")", "")
            curStr = curStr.replace(";", "")
            curStr = curStr.replace(".", "")
            oneOfStr += curStr + ";"
        
        for oneIdx in twoOfIdxArr:
            closeSemiIdx = 99999
            closebracketIdx = 99999
            for semiIdx in semiIdxArr:
                if semiIdx > oneIdx:
                    closeSemiIdx = semiIdx
                    break
            for closeBracket in closeBracketIdxArr:
                if closeBracket > oneIdx:
                    closebracketIdx = closeBracket
                    break
            betterIdx = min(closeSemiIdx, closebracketIdx)
            curStr = prereq[oneIdx:betterIdx]             
            curStr = curStr.replace("Two of", "2:")
            curStr = curStr.replace("two of", "2:")
            curStr = curStr.replace(" ", '')
            curStr = curStr.replace("/", ",")
            curStr = curStr.replace("or", ",")
            curStr = curStr.replace(")", "")
            curStr = curStr.replace(";", "")
            curStr = curStr.replace(".", "")
            oneOfStr += curStr + ";"
            
        
        for oneIdx in threeOfIdxArr:
            closeSemiIdx = 99999
            closebracketIdx = 99999
            for semiIdx in semiIdxArr:
                if semiIdx > oneIdx:
                    closeSemiIdx = semiIdx
                    break
            for closeBracket in closeBracketIdxArr:
                if closeBracket > oneIdx:
                    closebracketIdx = closeBracket
                    break
            betterIdx = min(closeSemiIdx, closebracketIdx)
            curStr = prereq[oneIdx:betterIdx]           
            curStr = curStr.replace("Three of", "3:")
            curStr = curStr.replace("three of", "3:")
            curStr = curStr.replace(" ", '')
            curStr = curStr.replace("/", ",")
            curStr = curStr.replace("or", ",")
            curStr = curStr.replace(")", "")
            curStr = curStr.replace(";", "")
            curStr = curStr.replace(".", "")
            oneOfStr += curStr + ";"
    return oneOfStr
    
    
def parseStrCourses(prereq: str, oneOfCourseList = [], courseList = []):
    courseIdxArr = []
    courseNameArr = []
    courseIdxNameMap = {}
    retvalList = []
    for course in courseList:
        course = course.replace(" ", "")
        if course == "ACTSC232" and prereq.find('or a corequisite of STAT230 or STAT240); Level at least 2A; Not open to students who have received credit for ACTSC232.') != -1:
            continue
        startIdx = prereq.find(course)
        if startIdx != -1:
            if startIdx == 0:
                endIdx = startIdx + len(course)
                if (endIdx < len(prereq)):
                    if prereq[endIdx] <= 'Z' and prereq[endIdx] >= 'A':
                        continue
            else:
                endIdx = startIdx + len(course)
                if (endIdx < len(prereq)):
                    if prereq[endIdx] <= 'Z' and prereq[endIdx] >= 'A':
                        continue
                if prereq[startIdx - 1] >= "A" and prereq[startIdx - 1] <= "Z":
                    continue
                    
            included = oneOfCourseList.count(course)
            if included != 0:
                continue
            courseIdxArr.append(startIdx)
            courseNameArr.append(course)
            courseIdxNameMap[startIdx] = course
    courseIdxArr.sort()
    for arrIdx, startIdx in enumerate(courseIdxArr):
        endIdx = startIdx + len(courseIdxNameMap[startIdx])
        if arrIdx == 0:
            retvalList.append(courseIdxNameMap[startIdx])
        if arrIdx == len(courseIdxArr) - 1:
            break
        else:
            curPartStr = prereq[endIdx:courseIdxArr[arrIdx + 1]]
            curCourse = courseIdxNameMap[startIdx]
            nextIdx = courseIdxArr[arrIdx + 1]
            nextCourse = courseIdxNameMap[nextIdx]
            if (curPartStr.find('or') != -1 or curPartStr.find('/') != -1):
                curStrIdx = len(retvalList) - 1
                newRetvalList = retvalList[:]
                for item in retvalList:
                    curCourse = courseIdxNameMap[startIdx]
                    item = item.replace(curCourse, nextCourse)
                    newRetvalList.append(item)
                retvalList = newRetvalList[:]
            elif curPartStr.find('and') != -1 or curPartStr.find(',') != -1:
                newList = []
                for item in retvalList:
                    item += ","
                    item += nextCourse
                    newList.append(item)
                retvalList = newList[:]
                    
    retval = ""
    for item in retvalList:
        retval += f'{item};'
    return retval
                
                
                

def produceStr(strList: list, courseList: list):
    retvalList = []
    initial = True
    
    for item in strList:
        if item == "":
            continue
        if len(retvalList) != 0:
            initial = False
        newCoursesList = item[2:].split(',')
        newNewCourseList = []
        for item2 in newCoursesList:
            if item2 in courseList:
                newNewCourseList.append(item2)
        newCoursesList = newNewCourseList[:]
        if item.find('1:') != -1:
            newRetvalList = []
            for idx in range(len(newCoursesList)):
                newRetvalList.append(newCoursesList[idx])
            if initial:
                for item in newRetvalList:
                    retvalList.append(item)
            else:
                newList = []
                for item in retvalList:
                    for course in newRetvalList:
                        newStr = item + "," + course
                        newList.append(newStr)
                retvalList = newList[:]
                
        elif item.find("2:") != -1:
            newRetvalList = []
            for idx in range(len(newCoursesList) - 1):
                for idxSecond in range(idx + 1, len(newCoursesList)):
                    newRetvalList.append(f"{newCoursesList[idx]},{newCoursesList[idxSecond]}")
            if initial:
                for item in newRetvalList:
                    retvalList.append(item)
            else:
                newList = []
                for item in retvalList:
                    for course in newRetvalList:
                        newStr = item + "," + course
                        newList.append(newStr)
                retvalList = newList[:]
                    
        elif item.find("3:") != -1:
            newRetvalList = []
            for idx in range(len(newCoursesList) - 2):
                for idxSecond in range(idx + 1, len(newCoursesList) - 1):
                    for idxThird in range(idx + 2, len(newCoursesList)):
                        newRetvalList.append(f"{newCoursesList[idx]},{newCoursesList[idxSecond]},{newCoursesList[idxThird]}")
            if initial:
                for item in newRetvalList:
                    retvalList.append(item)
            else:
                newList = []
                for item in retvalList:
                    for course in newRetvalList:
                        newStr = item + "," + course
                        newList.append(newStr)
                retvalList = newList[:]
    return retvalList
                
            
            
def addSpaceToStr(strList: list):
    retval = []
    for item in strList:
        splitIdx = 0
        for idx, char in enumerate(item):
            if char >= '0' and char <= '9':
                splitIdx = idx
                break
        courseStrNoSpace = item[:splitIdx] + ' ' + item[splitIdx:]
        retval.append(courseStrNoSpace.strip())
    return retval
            
                
                        

def parsePrereqs():
    courseList = []
    prereqList = []
    for key in coursePrereqDict.keys():
        courseList.append(key.upper())
    for key in coursePrereqDict.keys():
        prereq = coursePrereqDict[key]
        courseStr = ""
        finalCourseStr = ""
        prereqList.append(Prerequisite())
        prereqList[-1].courseID = key
        prereqList[-1].consentRequired = False
        if (prereq):
            courseStr += handleOneOf(prereq)
            oneOfcourseList = []
            firstCourseList = []
            if courseStr != "":
                firstCourseList = courseStr.split(";")
                for item in firstCourseList:
                    item = item.replace("1:", "")
                    item = item.replace("2:", "")
                    item = item.replace("3:", "")
                    curList = item.split(",")
                    for item2 in curList:
                        if len(item2) > 1:
                            oneOfcourseList.append(item2)
                
            retvalStr = parseStrCourses(prereq=prereq, oneOfCourseList=oneOfcourseList, courseList=courseList)
            courseListNoSpace = []
            for course in courseList:
                courseListNoSpace.append(course.replace(' ', ""))
            
            if courseStr != "":
                retvalList = produceStr(courseStr.split(";"), courseListNoSpace)
                retvalStrList = retvalStr.split(';')
                newResultList = []
                if len(retvalList) != 0:
                    for existItem in retvalStrList:
                        for newItem in retvalList:
                            if existItem == "":
                                newResultList.append(newItem)
                            else:
                                newResultList.append(existItem + "," + newItem)
                else:
                     for newItem in retvalStrList:
                         newResultList.append(newItem)
                    
                retvalStr = ""
                for item in newResultList:
                    retvalStr += f"{item};"
            newStr = ""
            for item in retvalStr.split(';'):
                itemList = item.split(',')
                newStrVal = addSpaceToStr(itemList)
                curStr = ""
                for item in newStrVal:
                    if item == "":
                        continue
                    curStr += f'{item},'
                if curStr != "":
                    curStr = curStr[:-1]
                    curStr += ";"
                    newStr += curStr
            prereqList[-1].courses = newStr
            levelAtLeast = ""
            if (prereq.find("Level at least ") != -1 or prereq.find("level at least ") != -1):
                level = prereq.split("evel at least ")[-1][0:2]
                if level in levels:
                    prereqList[-1].minimumLevel = level
    return prereqList
            
                
        
        
                
def getCourseFromUWflow(year: int):
    try:
        courseData = wrapperCourseDataFunc(year)
        prereqList = parsePrereqs()
        Session.add_all(courseData)
        Session.commit()
        Session.close()
        Session.add_all(prereqList)
        Session.commit()
        Session.close()
    except OperationalError as msg:
        print("Error: ", msg)

if __name__ == "__main__":
    getCourseFromUWflow(year=2023)