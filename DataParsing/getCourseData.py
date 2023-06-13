import requests
import re
from dotenv import dotenv_values
from multiprocessing.dummy import Pool

config = dotenv_values(".env")


class Course:
    def __init__(self, courseID, year, courseName, subject, code, description, credit, coreqs, antireqs, availability = "", isOnline = False, likedRating = 0, easyRating = 0, usefulRating = 0):
        self.courseID = courseID
        self.year = year
        self.courseName = courseName
        self.subject = subject
        self.code = code
        self.description = description
        self.credit = credit
        self.coreqs = coreqs
        self.antireqs = antireqs
        self.availability = availability
        self.isOnline = isOnline
        self.likedRating = likedRating
        self.easyRating = easyRating
        self.usefulRating = usefulRating

        
class prereq:
    def __init__(self, year, courses, minimumLevel, onlyOpenTo, notOpenTo, consentRequired = False):
        self.year = year
        self.courses = courses
        self.minimumLevel = minimumLevel
        self.onlyOpenTo = onlyOpenTo
        self.notOpenTo = notOpenTo
        self.consentRequired = consentRequired
        

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
            exDescription = '<div class="divTableCell colspan-2">[^/]*</div>'
            exDescription2 = '<div class="divTableCell colspan-2">[^<]*</div>'
            NumName = re.findall(exNumName, courseInfo)
            Prere = re.findall(exPre, courseInfo)
            Antire = re.findall(exAnti, courseInfo)
            coreq = re.findall(exCoreq, courseInfo)
            descriptions1 = re.findall(exDescription, courseInfo)
            descriptions2 = re.findall(exDescription2, courseInfo)
            assert(len(descriptions1) != 0 or len(descriptions2) != 0)
            realDescription = ""
            if (len(descriptions2) == 0):
                realDescription = descriptions1
            else:
                realDescription = descriptions2
            NumNameList = NumName[0].split('</strong>')
            infoList = NumNameList[0].split('</a>')[-1].split(' ')
            courseNum = infoList[0] + infoList[1]
            credit = infoList[-1].strip()
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
            # print(courseNum, nameInfo, AntiInfo, prereqInfo, coreqInfo, credit)
            antiStr = AntiInfo.split('Antireq: ')[-1]
            coreStr = coreqInfo.split("Coreq: ")[-1]
            subject = infoList[0]
            code = infoList[1]
    
            
            
            
            
            
        
        
        
        
        
if __name__ == "__main__":
    data = getUrl("https://ugradcalendar.uwaterloo.ca/page/Course-Descriptions-Index")
    urlList = parseCourseUrl(data)
    coursePages = getAllCoursePage(urlList)
    parseCourses(coursePages)
    