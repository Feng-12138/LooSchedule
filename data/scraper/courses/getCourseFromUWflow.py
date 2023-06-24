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
levels = ['1A', '1B', '2A', '2B', '3A']

config = dotenv_values(".env")

def getUrl(url: str, query = ""):
    data = ""
    try:
        if query == "":
            print(url)
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
                courseNum = (infoList[0] + infoList[1]).lower()
                credit = infoList[-1].strip()
            else:
                NumNameList = NumName[0].split('</strong>')
                infoList = NumNameList[0].split('</a>')[-1].split(' ')
                courseNum = (infoList[0] + infoList[1]).lower()
                credit = infoList[-1].strip()
            credit = float(credit)
            if courseDict.get(courseNum) == None:
                courseDict[courseNum] = Course()
                courseDict[courseNum].courseID = courseNum.upper()
                courseDict[courseNum].subject = infoList[0]
                courseDict[courseNum].code = infoList[1]
                courseDict[courseNum].credit = credit
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
        courseNum = course["code"]
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
            coursePrereq = Prerequisite()
            coursePrereq.courseID = courseNum
            coursePrereq.consentRequired = False
            # if courseDict[courseNum].prereqs.find("Level at least") != -1:
            #     coursePrereq.minimumLevel = courseDict[courseNum].prereqs.split("Level at least")[-1].strip()[:2]
            
                
                
                
    return courseDict.values()
                
def getCourseFromUWflow(year: int):
    courseData = wrapperCourseDataFunc(year)
    try:
        Session.add_all(courseData)
        Session.commit()
        Session.close()
    except OperationalError as msg:
        print("Error: ", msg)

if __name__ == "__main__":
    getCourseFromUWflow(year=2023)