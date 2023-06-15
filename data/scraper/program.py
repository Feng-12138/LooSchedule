from requests import get
from bs4 import BeautifulSoup
from re import compile
from sqlite3 import OperationalError
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey
from datetime import datetime
from settings import SESSION, BASE, MathDegreeRequirementsURL


baseURL = 'https://ugradcalendar.uwaterloo.ca'
programsURL = 'https://ugradcalendar.uwaterloo.ca/page/MATH-List-of-Academic-Programs-or-Plans'


class Requirement(BASE):
    __tablename__ = 'Requirement'
    type = Column(String, nullable=False)
    year = Column(String, nullable=False)
    courses = Column(String, nullable=False)
    additionalRequirements = Column(String, nullable=True)
    link = Column(String, nullable=True)

    def __init__(self, type, year, courses, additionalRequirements, link):
        self.type = type
        self.year = year
        self.courses = courses
        self.additionalRequirements = additionalRequirements
        self.links = link
    
    def insertDB(self, db):
        cursor = db.cursor()
        data = [self.type, self.year, self.courses, self.additionalRequirements, self.link]
        values = ('%s,' * len(data))[:-1]
        command = 'INSERT INTO Requirement VALUES (' + values +')'
        cursor.execute(command, data)


class Major(BASE):
    __tablename__ = 'Major'
    requirementID = Column(Integer, primary_key=True, nullable=False)
    majorName = Column(String, nullable=False)
    isCoop = Column(Boolean, nullable=False)
    isDoubleDegree = Column(Boolean, nullable=False)

    def __init__(self, requirementID, majorName, isCoop, isDoubleDegree):
        self.requirementID = requirementID
        self.majorName = majorName
        self.isCoop = isCoop
        self.isDoubleDegree = isDoubleDegree

    def insertDB(self, db):
        cursor = db.cursor()
        data = [self.requirementID, self.majorName, self.isCoop, self.isDoubleDegree]
        values = ('%s,' * len(data))[:-1]
        command = 'INSERT INTO Major VALUES (' + values +')'
        cursor.execute(command, data)



def getRequirement(year):
    html = get(programsURL).text
    soup = BeautifulSoup(html, features='html.parser')
    div = soup.find('span', class_='MainContent')
    programs = div.find_all('a')
    for program in programs:
        requirementURL = program['href']
        html = get(baseURL + requirementURL).text
        print(html)
        # TODO: fetch requirements for each programs

def getPrograms(db):
    html = get(programsURL).text
    soup = BeautifulSoup(html, features='html.parser')
    div = soup.find('span', class_='MainContent')
    programs = div.find_all('a')
    programEntity = []
    for program in programs:
        name = program.get_text()
        cursor = db.cursor()
        cursor.execute("SELECT requirementID FROM Requirement WHERE type=?", (name,))
        result = cursor.fetchone()
        programEntity.append(Major(result[0],program.get_text(), True, True))
