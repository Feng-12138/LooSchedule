from requests import get
from bs4 import BeautifulSoup
from re import compile


url = 'https://ugradcalendar.uwaterloo.ca/page/MATH-Degree-Requirements-for-Math-students'

class Communication:
    def __init__(self, courseID, year, listNum):
        self.courseID = courseID
        self.subject, self.code = courseID.split()
        self.listNumber = listNum
        self.year = str(year) + '-' + str(year + 1)

    def insertDB(self, db):
        cursor = db.cursor()
        data = [self.courseID, self.subject, self.code, self.listNumber, self.year]
        values = ('%s,' * len(data))[:-1]
        command = 'INSERT INTO Communication VALUES (' + values +')'
        cursor.execute(command, data)


def getCommunication(year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(url + param).text
    print(html)
    soup = BeautifulSoup(html, features='html.parser')
    ulList = soup.find_all('ul')
    skip = True if year > 2020 else False
    listNum = 1
    courses = []
    for ul in ulList:
        aList = ul.find_all('a', href=compile('courses'))
        if len(aList) == 0: continue
        if skip: skip = False
        else:
            for a in aList: courses.append(Communication(a.get_text(), year, listNum))
            if listNum == 2: break
            else: listNum += 1
    return courses
