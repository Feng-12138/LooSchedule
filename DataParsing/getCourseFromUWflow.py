import requests
import re
from dotenv import dotenv_values
from sqlite3 import OperationalError
from multiprocessing.dummy import Pool
from __init__ import Session, Base, engine
from sqlalchemy import Column, Integer, String, Boolean, DECIMAL
# from sqlalchemy.orm import relationship

# Base = __init__.Base

yearList = []
courseDict = {}

prereqList = []

config = dotenv_values(".env")

class Course(Base):
    __tablename__ = "Course"
    courseID = Column(String, primary_key=True, nullable=False)
    courseName = Column(String, nullable=False)
    subject = Column(String, nullable=False)
    code = Column(String, nullable=False)
    description = Column(String, nullable=False)
    credit = Column(DECIMAL, nullable=False)
    availability = Column(String)
    OnlineTerms = Column(String)
    coreqs = Column(String)
    antireqs = Column(String)
    likedRating = Column(DECIMAL)
    easyRating = Column(DECIMAL)
    usefulRating = Column(DECIMAL)
    

        
class prereq:
    def __init__(self, year, courses, minimumLevel, onlyOpenTo, notOpenTo, consentRequired = False):
        self.year = year
        self.courses = courses
        self.minimumLevel = minimumLevel
        self.onlyOpenTo = onlyOpenTo
        self.notOpenTo = notOpenTo
        self.consentRequired = consentRequired
        

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
            url = config["CourseCalendarBaseUrl"] + year + "/COURSE/" + courseStr
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

# we can not get prereq/antirq/coreq data directly from UWflow because they could change
# for example, for CS 2025, we do not need to take CS136L as coreq, but for CS 2027, they need
# UWflow only shows current data       
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
    uwFlowCourseList = getUrl(config["uwflowUrl"], query="""query Course {
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
                            if section["meetings"][0]["location"].find("ONLN") != -1 and onlineStr.find("S") == -1:
                                onlineStr += "S"
                    elif termId[-1] == "1" and availableStr.find("W") == -1:
                        availableStr += "W"
                        if (len(section["meetings"]) > 0):
                            if section["meetings"][0]["location"].find("ONLN") != -1 and onlineStr.find("W") == -1:
                                onlineStr += "W"
                    elif termId[-1] == "9" and availableStr.find("W") == -1:
                        availableStr += "F"
                        if (len(section["meetings"]) > 0):
                            if section["meetings"][0]["location"].find("ONLN") != -1 and onlineStr.find("F") == -1:
                                onlineStr += "F"
                courseDict[courseNum].availability = availableStr
                courseDict[courseNum].onlineTerms = onlineStr
    return courseDict.values()
                
def getCourseFromUWflow(year: int):
    courseData = wrapperCourseDataFunc(2023)
    try:
        Session.add_all(courseData)
        Session.commit()
        Session.close()
    except OperationalError as msg:
        print("Error: ", msg)

if __name__ == "__main__":
    getCourseFromUWflow(year=2023)