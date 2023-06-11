from requests import get
from bs4 import BeautifulSoup
from re import compile

baseURL = 'https://ugradcalendar.uwaterloo.ca'
programsURL = 'https://ugradcalendar.uwaterloo.ca/page/MATH-List-of-Academic-Programs-or-Plans'

class Major:
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

class Requirement:
    def __init__(self, type, year, courses, additionalRequirements, links):
        self.type = type
        self.year = year
        self.courses = courses
        self.additionalRequirements = additionalRequirements
        self.links = links
    
    def insertDB(self, db):
        cursor = db.cursor()
        data = [self.type, self.year, self.courses, self.additionalRequirements, self.links]
        values = ('%s,' * len(data))[:-1]
        command = 'INSERT INTO Requirement VALUES (' + values +')'
        cursor.execute(command, data)

def getRequirement(year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(programsURL + param).text
    soup = BeautifulSoup(html, features='html.parser')
    div = soup.find('span', class_='MainContent')
    programs = div.find_all('a')
    for program in programs:
        requirementURL = program['href']
        html = get(baseURL + requirementURL).text
        print(program.get_text())

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
