import sys
from requests import get
from bs4 import BeautifulSoup
from re import compile
from sqlite3 import OperationalError
from sqlalchemy import Column, Integer, String, ForeignKey
from datetime import datetime
from settings import SESSION, BASE, MathDegreeRequirementsURL


class Communication(BASE):
    __tablename__ = 'Communication'
    courseID = Column(String, primary_key=True, nullable=False) # ForeignKey('Course.courseID')
    year = Column(String, primary_key=True, nullable=False)
    subject = Column(String, nullable=False)
    code = Column(String, nullable=False)
    listNumber = Column(Integer, nullable=False)

    def __init__(self, courseID, year, listNum):
        self.courseID = courseID
        self.subject, self.code = courseID.split()
        self.listNumber = listNum
        self.year = str(year) + '-' + str(year + 1)


def getCommunicationData(year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(MathDegreeRequirementsURL + param).text
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
    try:
        SESSION.add_all(courses)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def getAllCommunicationData(start=2019):
    today = datetime.today()
    for year in range(start, today.year + 1):
        print(f'Collecting communication data for year {year}-{year + 1}!')
        getCommunicationData(year)


if __name__ == '__main__':
    getAllCommunicationData()
