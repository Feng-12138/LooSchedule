import requests
import re
from sqlite3 import OperationalError
from multiprocessing.dummy import Pool
from __init__ import Session
from schemas import Course, Prerequisite
import os


yearList = []
courseDict = {}
levels = ['1A', '1B', '2A', '2B', '3A', "3B", "4A", "4B"]

coursePrereqDict = {}


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
            url = "https://ucalendar.uwaterloo.ca/" + year + "/COURSE/" + courseStr
            urlList.append(url)
    return urlList

def getAllCoursePage(urlList: list):
    pool = Pool(6)
    retval = pool.map(getUrl, urlList)
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
    data = getUrl("https://ugradcalendar.uwaterloo.ca/page/Course-Descriptions-Index")
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
                courseDict[courseNum].coreqs = ""
                courseDict[courseNum].antireqs = ""
    uwFlowCourseList = getUrl("https://uwflow.com/graphql", query="""query Course {
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
            filled_count
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
        if courseDict.get(courseNum) != None or courseNum.find("BUS ") != -1:
            if (courseNum.find("BUS ") != -1):
                courseDict[courseNum] = Course()
                courseDict[courseNum].credit = 0.5
                courseDict[courseNum].courseID = courseNum
                courseDict[courseNum].subject = courseNum.split(" ")[0]
                courseDict[courseNum].code = courseNum.split(" ")[-1]
            courseDict[courseNum].courseName = course["name"]
            if course["description"] == None:
                courseDict[courseNum].description = ""
            else:
                courseDict[courseNum].description = course["description"]
            courseDict[courseNum].easyRating = course["rating"]["easy"]
            courseDict[courseNum].likedRating = course["rating"]["liked"]
            courseDict[courseNum].usefulRating = course['rating']['useful']
            courseDict[courseNum].coreqs = course["coreqs"]
            courseDict[courseNum].antireqs = course['antireqs']
            courseDict[courseNum].filledCount = course['rating']['filled_count']
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
    keys = courseDict.keys()
    for key in keys:
        coreq = courseDict[key].coreqs
        if coreq == None:
            continue
        parsedCoreq = parseByBracket(coreq, key, keys, False)
        courseDict[key].coreqs = parsedCoreq
    antireqStr = ""
    for key in keys:
        antireq = courseDict[key].antireqs
        if antireq == None:
            continue
        parseAntireq = antireq.split(",")
        parseAntireq_slash = []
        for course in parseAntireq:
            lst = course.split('/')
            parseAntireq_slash.extend(lst)
        parseAntireq = addSpaceToStr(parseAntireq_slash)
            
        finalAntireqStr = ""
        for item in parseAntireq:
            finalAntireqStr += item.upper().strip()
            finalAntireqStr += ","
        courseDict[key].antireqs = finalAntireqStr[:-1]
    return courseDict.values()
    

def modifyStr(courseStr: str, replacedStr: str) -> str:
    retval = ""
    start = ""
    if replacedStr == "One of":
        retval = courseStr.replace("One of", "1:")
        retval = retval.replace("one of", "1:")
        start = "1:"
    elif replacedStr == "Two of":
        retval = courseStr.replace("Two of", "2:")
        retval = retval.replace("two of", "2:")
        start = "2:"
    elif replacedStr == "Three of":
        retval = courseStr.replace("Three of", "3:")
        retval = retval.replace("three of", "3:")
        start = "3:"
        
    retval = retval.replace(" ", '')
    retval = retval.replace("/", ",")
    retval = retval.replace("or", ",")
    retval = retval.replace(")", "")
    retval = retval.replace("(", "")
    retval = retval.replace(";", "")
    retval = retval.replace(".", "")
    curStrList = retval[2:].split(",")
    retvalStrList = []
    for item in curStrList:
        encounterNum = False
        breakingIndex = len(item)
        for idx, char in enumerate(item):
            if (char >= "A" and char <= "Z"):
                continue
            elif char >= "0" and char <= "9":
                encounterNum = True
                continue
            if encounterNum:
                breakingIndex = idx
                break
        retvalStrList.append(item[:breakingIndex])
    retval = ""
    retval += start
    for val in retvalStrList:
        retval += f"{val},"
    retval = retval[:-1] + ';'
    return retval
            
            
        
    
def handleOneOf(prereq: str) -> tuple:
    oneOfOr = False
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
            if prereq[betterIdx:betterIdx + 4].find("or") != -1 or prereq[betterIdx:betterIdx + 4].find("/") != -1:
                oneOfOr = True
                
            curStr = prereq[oneIdx:betterIdx]             
            curStr = modifyStr(curStr, "One of")
            oneOfStr += curStr
        
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
            curStr = modifyStr(curStr, "Two of")
            oneOfStr += curStr
            
        
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
            curStr = modifyStr(curStr, "Three of")
            oneOfStr += curStr
    return oneOfOr, oneOfStr
    
    
def parseStrCourses(prereq: str, oneOfCourseList = [], courseList = []):
    courseIdxArr = []
    courseNameArr = []
    courseIdxNameMap = {}
    retvalList = []
    for course in courseList:
        course = course.replace(" ", "")
        startIdx = prereq.find(course)
        if startIdx != -1:
            if startIdx == 0:
                endIdx = startIdx + len(course)
                if (endIdx < len(prereq)):
                    if (prereq[endIdx] <= 'Z' and prereq[endIdx] >= 'A') or (prereq[endIdx] <= "9" and prereq[endIdx] >= "0"):
                        continue
            else:
                endIdx = startIdx + len(course)
                if (endIdx < len(prereq)):
                    if (prereq[endIdx] <= 'Z' and prereq[endIdx] >= 'A') or (prereq[endIdx] <= "9" and prereq[endIdx] >= "0"):
                        continue
                if prereq[startIdx - 1] >= "A" and prereq[startIdx - 1] <= "Z" or (prereq[startIdx - 1] <= "9" and prereq[startIdx - 1] >= "0"):
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
                
                
                
# produce list for one of, two of, three of
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

def produceCourseStrList(courseStr: str):
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
    return oneOfcourseList
    

# parse the prereq according to precedence of bracket, or, and
def parseByBracket(prereq: str, courseCode: str, courseList: list, forPrereq = True):
    invalidCourseList = []
    if prereq.find("Not open") != -1:
        notOpenStr = prereq.split("Not open")[-1]
        for course in courseList:
            course = course.replace(" ", "")
            if notOpenStr.find(course) != -1:
                invalidCourseList.append(course)
    
    parsedByBracketList = prereq.split(')')
    parsedByOpenBracket = False
    if len(parsedByBracketList) > 2:
        for idx, item in enumerate(parsedByBracketList):
            if item == "" and idx != len(parsedByBracketList) - 1:
                parsedByOpenBracket = True
                break
    parsedBySemiColon = False
    if len(parsedByBracketList) == 1:
        parsedBySemiColon = True
        parsedByBracketList = prereq.split(';')
    if parsedByOpenBracket:
        parsedByBracketList = prereq.split('(')
        
    orConnectionsBetweenBracket = []
    andConnectionBetweeenBracket = []
    parsedStrList = []
    parsedBracketStrList = []
    for idx, item in enumerate(parsedByBracketList):
        item = item.strip()
        if not parsedByOpenBracket:
            if item[:2] == "or" or item[:1] == "/":
                orConnectionsBetweenBracket.append(idx)
            elif item[:3] == "and" or item[:1] == ";" or parsedBySemiColon or item[:1] == ",":
                andConnectionBetweeenBracket.append(idx)
        else:
            if item[-2:] == "or" or item[-1:] == "/":
                orConnectionsBetweenBracket.append(idx + 1)
            elif item[-3:] == "and" or item[-1:] == ";" or item[-1:] == ",":
                andConnectionBetweeenBracket.append(idx + 1)
            
        oneofOr, courseStr = handleOneOf(item)
        oneOfcourseList = produceCourseStrList(courseStr=courseStr)
        oneOfcourseList.extend(invalidCourseList)
        retvalStr = parseStrCourses(prereq=item, oneOfCourseList=oneOfcourseList, courseList=courseList)
        courseListNoSpace = []
        for course in courseList:
            courseListNoSpace.append(course.replace(' ', ""))
        orConnection = False
        if item.find("or one of") != -1:
            orConnection = True
        # courseStr is the list of courses which has one of in it.
        retvalStrList = retvalStr.split(';')[:-1]
        retvalStrListCombined = []
        if courseStr != "":
            retvalList = produceStr(courseStr.split(";"), courseListNoSpace)
            if (orConnection):
                for val in retvalList:
                    retvalStrList.append(val)
            else:
                if len(retvalStrList) == 0:
                    retvalStrListCombined = retvalList[:]
                elif (len(retvalList) == 0):
                    retvalStrListCombined = retvalStrList[:]
                else:
                    for item in retvalStrList:
                        for val in retvalList:
                            retvalStrListCombined.append(item + "," + val)
        else:
            retvalStrListCombined = retvalStrList[:]
        parsedBracketStrList.append(retvalStrListCombined)
    finalReturnList = []
    finalReturnStr = ""
    for idx, parsedList in enumerate(parsedBracketStrList):
        if parsedList == []:
            continue
        if len(finalReturnList) == 0:
            finalReturnList.extend(parsedList)
            continue
        if idx in orConnectionsBetweenBracket:
            for item in parsedList:
                finalReturnList.append(item)
        elif (idx in andConnectionBetweeenBracket):
            newfinalList = []
            for item in parsedList:
                for existedItem in finalReturnList:
                    newfinalList.append(existedItem + "," + item)
            finalReturnList = newfinalList[:]
    for item in finalReturnList:
        finalReturnStr += f"{item};"
    # add space between code and course:
    finalReturnStrList = finalReturnStr.split(";")
    addedPlaceStr = ""
    for item in finalReturnStrList:
        if item == "":
            continue
        courseList = item.split(",")
        newCourseList = addSpaceToStr(courseList)
        newCourseStr = ""
        for modifiedCourse in newCourseList:
            newCourseStr += f"{modifiedCourse},"
        newCourseStr =  newCourseStr[:-1] + ";"
        addedPlaceStr += newCourseStr
        
    # inaccurate info from uwflow
    if (courseCode == "CS 245"):
        addedPlaceStr += "CS 136,MATH 145;CS 138,MATH 145;CS 146,MATH 145;"
    if (courseCode == "PMATH 333" and forPrereq):
        addedPlaceStr = "MATH 128;MATH 138;MATH 148;"
    return addedPlaceStr
    
                

def parsePrereqs(programList: list):
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
            newStr = parseByBracket(prereq=prereq, courseCode=key, courseList=courseList)
            prereqList[-1].courses = newStr
            levelAtLeast = ""
            onlyPattern = ""
            onlyOpenToStr = ""
            notOpenToStr = ""
            if (prereq.find(" Only ") != -1):
                onlyPattern = " Only "
            if (prereq.find(" only ") != -1):
                onlyPattern = " only "
            if (prereq.find(" only.") != -1):
                onlyPattern = " only."
            notOpenPattern = ""
            if (prereq.find("Not open") != -1):
                notOpenPattern = "Not open"
            if (prereq.find("not open") != -1):
                notOpenPattern = "not open"
            if (onlyPattern != "" and notOpenPattern != ""):
                pass
            elif (onlyPattern != ""):
                onlyList = prereq.split(onlyPattern)
                for item in onlyList:
                    for program in programList:
                        if item.find(program) != -1:
                            if program == "Engineering Students":
                                if onlyOpenToStr.find("Engineering") != -1:
                                    continue
                                else:
                                    program = "Faculty of Engineering"
                            if program == "Arts students":
                                if onlyOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Arts"
                            if program == "Science students":
                                if onlyOpenToStr.find("Science") != -1:
                                    continue
                                program = "Faculty of Science"
                            if program == "Environment students":
                                if onlyOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Environment"
                            if program == "Health students":
                                if onlyOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Health"
                            if program == "Mathematics students":
                                if onlyOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Mathematics"
                            if program == "Honours Math":
                                if onlyOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Mathematics"
                            onlyOpenToStr += f"{program},"
                onlyOpenToStr = onlyOpenToStr[:-1]
            elif (notOpenPattern != ""):
                notOpenList = prereq.split(notOpenPattern)
                for item in notOpenList:
                    for program in programList:
                        if item.find(program) != -1:
                            if program == "Engineering Students":
                                if notOpenToStr.find("Engineering") != -1:
                                    continue
                                else:
                                    program = "Faculty of Engineering"
                            if program == "Arts students":
                                if notOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Arts"
                            if program == "Science students":
                                if notOpenToStr.find("Science") != -1:
                                    continue
                                program = "Faculty of Science"
                            if program == "Environment students":
                                if notOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Environment"
                            if program == "Health students":
                                if notOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Health"
                            if program == "Mathematics students":
                                if notOpenToStr.find(program) != -1:
                                    continue
                                program = "Faculty of Mathematics"
                            if program == "Honours Math":
                                if notOpenToStr.find(program) != -1:
                                    continue
                            program = "Faculty of Mathematics"
                            notOpenToStr += f"{program},"
            else:
                for program in programList:
                    if prereq.find(program) != -1:
                        if program == "Engineering Students":
                            if onlyOpenToStr.find("Engineering") != -1:
                                continue
                            else:
                                program = "Faculty of Engineering"
                        if program == "Arts students":
                            if onlyOpenToStr.find(program) != -1:
                                continue
                            program = "Faculty of Arts"
                        if program == "Science students":
                            if onlyOpenToStr.find("Science") != -1:
                                continue
                            program = "Faculty of Science"
                        if program == "Environment students":
                            if onlyOpenToStr.find(program) != -1:
                                continue
                            program = "Faculty of Environment"
                        if program == "Health students":
                            if onlyOpenToStr.find(program) != -1:
                                continue
                            program = "Faculty of Health"
                        if program == "Mathematics students":
                            if onlyOpenToStr.find(program) != -1:
                                continue
                            program = "Faculty of Mathematics"
                        if program == "Honours Math":
                            if onlyOpenToStr.find(program) != -1:
                                continue
                            program = "Faculty of Mathematics"
                        onlyOpenToStr += f"{program},"
                onlyOpenToStr = onlyOpenToStr[:-1]
            if (prereq.find("Engineering") != -1):
                notOpenToStr += f"Faculty of Mathematics,"
            if len(notOpenToStr) >= 1:
                notOpenToStr = notOpenToStr[:-1]
            prereqList[-1].notOpenTo = notOpenToStr
            prereqList[-1].onlyOpenTo = onlyOpenToStr
                
            if (prereq.find("Level at least ") != -1 or prereq.find("level at least ") != -1):
                level = prereq.split("evel at least ")[-1][0:2]
                if level in levels:
                    prereqList[-1].minimumLevel = level
    return prereqList
            
def parsePrograms():
    retvalList = []
    htmlStr = getUrl("https://uwaterloo.ca/future-students/programs/alpha-list")
    contentStr = htmlStr.split('<div> class="content">')[-1]
    contentStr = contentStr.split('<table class="tablesaw tablesaw-stack"')[0]
    contentStrList = contentStr.split("<li><a")
    for item in contentStrList:
        item = item.split('</a></li>')[0].split(">")[-1]
        if item.find("\n") != -1:
            continue
        retvalList.append(item)
    retvalList = retvalList[8:]
    retvalList.append("Faculty of Mathematics")
    retvalList.append("Faculty of Arts")
    retvalList.append("Faculty of Health")
    retvalList.append("Faculty of Engineering")
    retvalList.append("Faculty of Environment")
    retvalList.append("Faculty of Science")
    retvalList.append("Mathematics students")
    retvalList.append("Arts students")
    retvalList.append("Health students")
    retvalList.append("Engineering students ")
    retvalList.append("Environment students")
    retvalList.append("Science students")
    retvalList.append("Honours Math")
    return retvalList
    
    
        
        
                
def getCourseFromUWflow(year: int):
    try:
        courseData = wrapperCourseDataFunc(year)
        program_list = parsePrograms()
        prereqList = parsePrereqs(programList=program_list)
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