import requests
import re
from dotenv import dotenv_values
from multiprocessing.dummy import Pool

config = dotenv_values(".env")


class Course:
    def __init__(self, courseName, courseNum, preList, antiList, restrictProgram, crossList, credit, restrictLevel):
        self.courseName = courseName
        self.courseNum = courseNum
        self.preList = preList
        self.antiList = antiList
        self.restrictProgram = restrictProgram
        self.crossList = crossList
        self.credit = credit
        self.restrictLevel = restrictLevel
        

def getUrl(url: str):
    data = ""
    try:
        res = requests.get(url=url)
        data = res.text
    except ValueError:
        print("error when obtaining course urls")
    return data

def parseCourseUrl(data: str):
    ex = '<a href="/courses/[a-zA-Z]*">'
    urlList = []
    lst = re.findall(ex, data)
    for item in lst:
        url = item.split('<a href="/courses/')[-1].split('">')[0]
        courseStr = f"course-{url}.html"
        url = config["CourseCalendarBaseUrl"] + courseStr
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
    for courseInfoLst in courseInfoStrList:
        for courseInfo in courseInfoLst:
            exNumName = "<strong>.*</strong>"
            exPre = "Prereq:.*</em>"
            exAnti = "Antireq:.*</em>"
            exCross = "Cross-Listed:.*</em>"
            exCoreq = "Coreq:.*</em>"
            exOffer = "Offered:.*]</div>"
            NumName = re.findall(exNumName, courseInfo)
            Prere = re.findall(exPre, courseInfo)
            Antire = re.findall(exAnti, courseInfo)
            Cross = re.findall(exCross, courseInfo)
            coreq = re.findall(exCoreq, courseInfo)
            NumNameList = NumName[0].split('</strong>')
            infoList = NumNameList[0].split('</a>')[-1].split(' ')
            courseNum = infoList[0] + " " + infoList[1]
            credit = infoList[-1]
            nameInfo = NumNameList[-2].split('<strong>')[-1]
            prereqInfo = ""
            AntiInfo = ""
            coreqInfo = ""
            if (len(Antire) != 0):
                AntiInfo = Antire[0].split('</em>')[0]
            if (len(Prere)):
                prereqInfo = Prere[0].split('</em>')[0]
            if (len(coreq)):
                coreqInfo = coreq[0].split('</em>')[0]
            restrictLevel = ""
            if (prereqInfo.find("Level at least ") != -1):
                restrictLevel = prereqInfo.split("Level at least ")[-1][0:2]
            # not open needs to be identified in 
            print(courseNum, nameInfo, AntiInfo, prereqInfo, coreqInfo)
        
        
        
        
        
if __name__ == "__main__":
    data = getUrl("https://ugradcalendar.uwaterloo.ca/page/Course-Descriptions-Index")
    urlList = parseCourseUrl(data)
    coursePages = getAllCoursePage(urlList)
    parseCourses(coursePages)
    