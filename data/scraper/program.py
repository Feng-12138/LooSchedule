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

    def __init__(self, requirementID, type, year, courses, additionalRequirements, link):
        self.requirementID = requirementID
        self.type = type
        self.year = year
        self.courses = courses
        self.additionalRequirements = additionalRequirements
        self.link = link


class Major(BASE):
    __tablename__ = 'Major'
    requirementID = Column(Integer, ForeignKey('Requirement.requirementID'), primary_key=True, nullable=False)
    majorName = Column(String, nullable=False)
    coopOnly = Column(Boolean, nullable=False)
    isDoubleDegree = Column(Boolean, nullable=False)

    def __init__(self, requirementID, majorName, coopOnly, isDoubleDegree):
        self.requirementID = requirementID
        self.majorName = majorName
        self.coopOnly = coopOnly
        self.isDoubleDegree = isDoubleDegree


class Minor(BASE):
    __tablename__ = 'Minor'
    requirementID = Column(Integer, ForeignKey('Requirement.requirementID'), primary_key=True, nullable=False)
    minorName = Column(String, nullable=False)
    
    def __init__(self, requirementID, minorName):
        self.requirementID = requirementID
        self.minorName = minorName


class Specialization(BASE):
    __tablename__ = 'Specialization'
    requirementID = Column(Integer, ForeignKey('Requirement.requirementID'), primary_key=True, nullable=False)
    specializationName = Column(String, nullable=False)
    
    def __init__(self, requirementID, specializationName):
        self.requirementID = requirementID
        self.specializationName = specializationName


class Joint(BASE):
    __tablename__ = 'Joint'
    requirementID = Column(Integer, ForeignKey('Requirement.requirementID'), primary_key=True, nullable=False)
    jointName = Column(String, nullable=False)
    
    def __init__(self, requirementID, jointName):
        self.requirementID = requirementID
        self.jointName = jointName


def addBCS2023():
    courses = '1:CS 115,CS 135,CS 145;1:CS 136,CS 146;1:MATH 127,MATH 137,MATH 147;1:MATH 128,MATH 138,MATH 148;1:MATH 135,MATH 145;1:MATH 136,MATH 146;1:MATH 239,MATH 249;1:STAT 230,STAT 240;1:STAT 231,STAT 241;1:CS 136L;1:CS 240,CS 240E;1:CS 241,CS 241E;1:CS 245,CS 245E;1:CS 246,CS 246E;1:CS 251,CS 251E;1:CS 341;1:CS 350;3:CS 340-CS 398,CS 440-CS 489;2:CS 440-CS 489;1:CO 487,CS 440-CS 498,CS 499T,STAT 440,CS 6xx,CS 7xx'
    r = Requirement(
        1,
        'major', 
        '2023-2024', 
        courses, 
        'Breath and Depth Required', 
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Bachelor-of-Computer-Science-1'
    )
    m = Major(1, 'Bachelor of Computer Science', False, False)
    try:
        SESSION.add(r)
        SESSION.add(m)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def addBMATHCS2023():
    courses = '1:CS 115,CS 135,CS 145;1:CS 116,CS 136,CS 146;1:MATH 135,MATH 145;1:MATH 106,MATH 136,MATH 146;1:MATH 127,MATH 137,MATH 147;1:MATH 128,MATH 138,MATH 148;1:MATH 235,MATH 245;1:MATH 237,MATH 247;1:MATH 239,MATH 249;1:STAT 230,STAT 240;1:STAT 231,STAT 241;1:CS 240,CS 240E;1:CS 241,CS 241E;1:CS 245,CS 245E;1:CS 246,CS 246E;1:CS 251,CS 251E;1:CS 341;1:CS 350;1:CS 371,CS 370;1:CS 360,CS 365;1:CS 340-CS 398,CS 440-CS 489;2:CS 440-CS 489;1:CO 487,CS 440-CS 498,CS 499T,STAT 440,CS 6xx,CS 7xx'
    r = Requirement(
        2,
        'major', 
        '2023-2024', 
        courses, 
        'Breath and Depth Required', 
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Bachelor-of-Mathematics-Computer-Science-1'
    )
    m = Major(2, 'Bachelor of Mathematics (Computer Science)', False, False)
    try:
        SESSION.add(r)
        SESSION.add(m)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def addSTATMinor2023():
    courses = '1:MATH 237,MATH 247;3:STAT 330,STAT 331,STAT 332,STAT 333;2:STAT 3xx,STAT 4xx'
    r = Requirement(
        3,
        'minor',
        '2023-2024',
        courses,
        None,
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Statistics-Minor'
    )
    m = Minor(3, 'Statistics Minor')
    try:
        SESSION.add(r)
        SESSION.add(m)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def addCOMinor2023():
    courses = '1:MATH 103,MATH 106,MATH 114,MATH 115,MATH 136,MATH 146;1:MATH 135,MATH 145;1:MATH 104,MATH 116,MATH 117,MATH 127,MATH 137,MATH 147;1:CO 250,CO 255;1:MATH 239,MATH 249;3:CO 330,CO 331,CO 342,CO 351,CO 342,CO 351,CO 353,CO 367,CO 370,CO 372,CO 430,CO 431,CO 432,CO 434,CO 439,CO 440,CO 442,CO 444,CO 446,CO 450,CO 452,CO 454,CO 456,CO 459,CO 463,CO 466,CO 471,CO 481,CO 485,CO 486,CO 487'
    r = Requirement(
        4,
        'minor',
        '2023-2024',
        courses,
        None,
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Combinatorics-and-Optimization-Minor2'
    )
    m = Minor(4, 'Combinatorics and Optimization Minor')
    try:
        SESSION.add(r)
        SESSION.add(m)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def addAISpec2023():
    courses = '1:CS 486,CS 492,CS 480;4:CO 367,CO 456,CO 463,CO 466,CS 452,CS 479,CS 480,CS 484,CS 485,STAT 341,STAT 440,STAT 441,STAT 444,ECE 380,SE 380,ECE 423,ECE 457C,ECE 481,ECE 486,ECE 488,ECE 495,MTE 544,SYDE 552,SYDE 556,SYDE 572'
    r = Requirement(
        5,
        'specialization',
        '2023-2024',
        courses,
        None,
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Computer-Sci-Artificial-Intelligence-Spec'
    )
    s = Specialization(5, 'Artificial Intelligence Specialization')
    try:
        SESSION.add(r)
        SESSION.add(s)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


def addBusSpec2023():
    courses = '2:CS 348,CS 454,CS 490;6:ACTSC 231,ACTSC 372,AFM 101,AFM 123,AFM 102,AFM 131,ARBUS 302,BUS 121W,BUS 362W,BUS 481W,BUS 491W,COMM 400,ECON 101,ECON 102,HRM 200,MGMT 220,MSCI 211,PSYCH 238,MSCI 311,MSCI 452'
    r = Requirement(
        6,
        'specialization',
        '2023-2024',
        courses,
        None,
        'http://ugradcalendar.uwaterloo.ca/page/MATH-Computer-Sci-Business-Spec'
    )
    s = Specialization(6, 'Business Specialization')
    try:
        SESSION.add(r)
        SESSION.add(s)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)
    
if __name__ == '__main__':
    addBCS2023()
    addBMATHCS2023()
    addSTATMinor2023()
    addCOMinor2023()
    addAISpec2023()
    addBusSpec2023()

# def getProgramRequirements(name, url, year):
#     param = '?ActiveDate=9/1/' + str(year)
#     html = get(UndergradCalendarBaseURL + url + param).text
#     soup = BeautifulSoup(html, features='html.parser')
#     requirements = soup.find('span', id='ctl00_contentMain_lblContent')
#     aList = requirements.find_all('a', href=compile('page'))
#     for a in aList[:3]:
#         planName, url = a.get_text(), a['href']
#         if not validatePlan(planName): continue
#         parentTag = a.find_previous()
#         if parentTag and parentTag.next_sibling and parentTag.next_sibling.name == 'ul':
#             url = parentTag.next_sibling.find('a', text='Degree Requirements')['href']
#         getRequirement(planName, url, year)


# def validatePlan(planName):
#     if planName == 'Admissions': return False
#     if planName == 'Plan Requirements': return False
#     if planName == 'Specializations': return False
#     if planName == 'Overview': return False
#     if planName == 'Degree Requirements': return False
#     return True


# def getRequirement(name, url, year):
#     param = '?ActiveDate=9/1/' + str(year)
#     html = get(UndergradCalendarBaseURL + url + param).text
#     soup = BeautifulSoup(html, features='html.parser')
#     contents = soup.find('span', id='ctl00_contentMain_lblContent')
#     aList = [a.get_text() for a in contents.find_all('a')]
#     res, courses = [], set()
#     if 'Table 1' in aList and 'Table 2' in aList: res += getTable2Courses(year)
#     choices = contents.find_next('ul').contents
#     choices = list(filter(lambda c: c != '\n', choices))
#     for choice in choices[:5]:
#         r, c = parseChoice(choice)
#         res += r
#         courses = courses.union(c)
#     # res = updateRequirement(res)
#     # print(res)


# def getTable2Courses(year):
#     param = '?ActiveDate=9/1/' + str(year)
#     html = get(MathDegreeRequirementsURL + param).text
#     soup = BeautifulSoup(html, features='html.parser')
#     choices = soup.find(string='Table 2 – Faculty Core Courses').find_next('ul').contents
#     choices = list(filter(lambda c: c != '\n', choices))
#     res, courses = [], set()
#     for choice in choices:
#         r, c = parseChoice(choice)
#         res += r
#         courses = courses.union(set(c))
#     print(res)
#     print(courses)
#     return res, courses


# def updateRequirement(requirement, courses, choice):
#     r, _ = parseChoice(choice)
#     for req in r:
#         n, options = req[0], req[1]
#         for option in options:
#             if option in courses:
#                 print(option)

#     # for i, req in enumerate(requirement):
#     #     n, options = req[0], req[1]
#     #     for j, r in enumerate(requirement):
#     #         if j == i: continue
#     #         count = 0
#     #         for option in options:
#     #             if option in r[1]: count += 1
#     #         print(count)


# def parseChoice(choice):
#     logic = choice.contents[0].lower()
#     options = [course.find('a').get_text() for course in choice.find_all('li')]
#     n, res, courses = 0, [], []
#     if logic == compile('all'):
#         for option in options: 
#             res.append((1, option))
#             courses.append(option)
#         return res
#     elif 'one' in logic: n = 1
#     elif logic == compile('two'): n = 2
#     elif logic == compile('three'): n = 3
#     elif logic == compile('four'): n = 4
#     elif logic == compile('five'): n = 5
#     elif logic == compile('six'): n = 6
#     elif logic == compile('seven'): n = 7
#     elif logic == compile('eight'): n = 8
#     elif logic == compile('nine'): n = 9
#     elif logic == compile('ten'): n = 10
#     else: 
#         print(f'Invalid number for:\n{logic}')
#         return res
#     res.append((n, options))
#     for option in options: courses.append(option)
#     return res, courses
    

# if __name__ == '__main__':
#     getTable2Courses(2023)
#     # getProgramRequirements('Computer Science', '/group/MATH-Computer-Science-1', 2023)
#     getRequirement('Bachelor of Computer Science', '/page/MATH-Bachelor-of-Computer-Science-1', 2023)