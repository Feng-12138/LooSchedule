from requests import get
from bs4 import BeautifulSoup
from re import compile
from sqlite3 import OperationalError
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey
from datetime import datetime
from settings import SESSION, BASE, UndergradCalendarBaseURL, MathDegreeRequirementsURL


baseURL = 'https://ugradcalendar.uwaterloo.ca'
programsURL = 'https://ugradcalendar.uwaterloo.ca/page/MATH-List-of-Academic-Programs-or-Plans'


class Requirement(BASE):
    __tablename__ = 'Requirement'
    requirementID = Column(Integer, primary_key=True, nullable=False)
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
    
    # def insertDB(self, db):
    #     cursor = db.cursor()
    #     data = [self.type, self.year, self.courses, self.additionalRequirements, self.link]
    #     values = ('%s,' * len(data))[:-1]
    #     command = 'INSERT INTO Requirement VALUES (' + values +')'
    #     cursor.execute(command, data)


class Major(BASE):
    __tablename__ = 'Major'
    requirementID = Column(Integer, ForeignKey('Requirement.requirementID'), primary_key=True, nullable=False)
    majorName = Column(String, nullable=False)
    coopOnly = Column(Boolean, nullable=False)
    isDoubleDegree = Column(Boolean, nullable=False)

    def __init__(self, requirementID, majorName, isCoop, isDoubleDegree):
        self.requirementID = requirementID
        self.majorName = majorName
        self.isCoop = isCoop
        self.isDoubleDegree = isDoubleDegree

    # def insertDB(self, db):
    #     cursor = db.cursor()
    #     data = [self.requirementID, self.majorName, self.isCoop, self.isDoubleDegree]
    #     values = ('%s,' * len(data))[:-1]
    #     command = 'INSERT INTO Major VALUES (' + values +')'
    #     cursor.execute(command, data)


# def getRequirement(year):
#     html = get(programsURL).text
#     soup = BeautifulSoup(html, features='html.parser')
#     div = soup.find('span', class_='MainContent')
#     programs = div.find_all('a')
#     for program in programs:
#         requirementURL = program['href']
#         print(requirementURL)
#         html = get(UndergradCalendarBaseURL + requirementURL).text
#         # print(html)
#         # TODO: fetch requirements for each programs

# def getPrograms(db):
#     html = get(programsURL).text
#     soup = BeautifulSoup(html, features='html.parser')
#     div = soup.find('span', class_='MainContent')
#     programs = div.find_all('a')
#     programEntity = []
#     for program in programs:
#         name = program.get_text()
#         cursor = db.cursor()
#         cursor.execute("SELECT requirementID FROM Requirement WHERE type=?", (name,))
#         result = cursor.fetchone()
#         programEntity.append(Major(result[0],program.get_text(), True, True))


def getAcademicPlans(start=2019):
    url = 'http://ugradcalendar.uwaterloo.ca/group/MATH-Academic-Plans-and-Requirements'
    html = get(url).text
    soup = BeautifulSoup(html, features='html.parser')
    plans = soup.find('span', id='ctl00_contentMain_lblContent').find_all('li')
    for plan in plans[2:]:
        name = plan.get_text()
        if name == 'Plans for Students outside the Mathematics Faculty': continue
        url = plan.find('a')['href']
        print(name, url)
        today = datetime.today()
        # for year in range(start, today.year + 1):
        #     getPlanRequirements(name, url, year)


def getPlanRequirements(name, url, year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(UndergradCalendarBaseURL + url + param).text
    soup = BeautifulSoup(html, features='html.parser')
    requirements = soup.find('span', id='ctl00_contentMain_lblContent')
    # plans = [req.find('a').get_text() for req in requirements[1:]]
    print(html)


def getTable2Courses(year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(MathDegreeRequirementsURL + param).text
    soup = BeautifulSoup(html, features='html.parser')
    choices = soup.find(string='Table 2 – Faculty Core Courses').find_next('ul').contents
    choices = list(filter(lambda c: c != '\n', choices))
    res = ''
    for choice in choices: res += parseChoice(choice)
    return res


def parseChoice(choice):
    logic = choice.contents[0].lower()
    options = [course.find('a').get_text() for course in choice.find_all('li')]
    n, res = 0, ''
    if logic == compile('all'):
        for option in options: res += '1:' + option + ';'
        return res
    elif 'one' in logic: n = 1
    elif logic == compile('two'): n = 2
    elif logic == compile('three'): n = 3
    elif logic == compile('four'): n = 4
    elif logic == compile('five'): n = 5
    elif logic == compile('six'): n = 6
    elif logic == compile('seven'): n = 7
    elif logic == compile('eight'): n = 8
    elif logic == compile('nine'): n = 9
    elif logic == compile('ten'): n = 10
    else: 
        print(f'Invalid number for:\n{logic}')
        return res
    res += str(n) + ':'
    for option in options: res += option + ','
    res = res[:-1] + ';'
    return res
    

if __name__ == '__main__':
    print(getTable2Courses(2023))
    # getAcademicPlans()
    getPlanRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2023)
