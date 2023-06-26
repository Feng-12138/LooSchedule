from requests import get
from bs4 import BeautifulSoup
from re import compile
from sqlite3 import OperationalError
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey
from datetime import datetime
from settings import SESSION, BASE, UndergradCalendarBaseURL, MathDegreeRequirementsURL


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


def getAcademicPrograms(start=2019):
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


def getProgramRequirements(name, url, year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(UndergradCalendarBaseURL + url + param).text
    soup = BeautifulSoup(html, features='html.parser')
    requirements = soup.find('span', id='ctl00_contentMain_lblContent')
    aList = requirements.find_all('a', href=compile('page'))
    for a in aList[:3]:
        planName, planUrl = a.get_text(), a['href']
        if not validatePlan(planName): continue
        parentTag = a.find_previous()
        if parentTag and parentTag.next_sibling and parentTag.next_sibling.name == 'ul':
            url = parentTag.next_sibling.find('a', text='Degree Requirements')['href']
        print(planName, planUrl)
        getRequirement(planName, planUrl, year)


def validatePlan(planName):
    if planName == 'Admissions': return False
    if planName == 'Plan Requirements': return False
    if planName == 'Specializations': return False
    if planName == 'Overview': return False
    if planName == 'Degree Requirements': return False
    return True


def getRequirement(name, url, year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(UndergradCalendarBaseURL + url + param).text
    soup = BeautifulSoup(html, features='html.parser')
    contents = soup.find('span', id='ctl00_contentMain_lblContent')
    aList = [a.get_text() for a in contents.find_all('a')]
    res, courses = [], set()
    if 'Table 2' in aList: 
        res, courses = getTable2Courses(year)
    choices = contents.find_next('ul').contents
    choices = list(filter(lambda c: c != '\n', choices))
    for choice in choices:
        res, courses = updateRequirement(res, courses, choice)
    print(res, courses)
    return res


def getTable2Courses(year):
    param = '?ActiveDate=9/1/' + str(year)
    html = get(MathDegreeRequirementsURL + param).text
    soup = BeautifulSoup(html, features='html.parser')
    choices = soup.find(string='Table 2 – Faculty Core Courses').find_next('ul').contents
    choices = list(filter(lambda c: c != '\n', choices))
    res, courses = [], set()
    for choice in choices:
        r, c, _ = parseChoice(choice)
        res += r
        courses = courses.union(set(c))
    return res, courses


def updateRequirement(requirement, courses, choice):
    r, c, a = parseChoice(choice)
    if a: 
        requirement.append(r)
        courses = courses.union(c)
        return requirement, courses
    for (n, options) in r:
        duplicates = set()
        for option in options:
            if option in courses:
                duplicates.add(option)
            else: courses.add(option)
        if len(duplicates) > 0:
            reduced = []
            for req in requirement:
                duplicated = False
                for dup in duplicates:
                    if dup in req[1]: 
                        duplicated = True
                        break
                if duplicated:
                    requirement.remove(req)
                    m = min(n, req[0])
                    requirement.append((m, list(set(req[1]).intersection(duplicates))))
                    print((m, list(set(req[1]).intersection(duplicates))))
                    if n - req[0] != 0:
                        reduced.append((abs(n - req[0]), list(set(req[1]).symmetric_difference(duplicates))))
            requirement += reduced
        else:
            requirement.append((n, options))
            courses = courses.union(set(options))
    return requirement, courses


def parseChoice(choice):
    logic = choice.contents[0].lower()
    options = [course.find('a').get_text() for course in choice.find_all('li')]
    n, res, additional = 0, [], False
    if 'additional' in logic: additional = True
    if 'all' in logic:
        for option in options: res.append((1, [option]))
        return res, options, additional
    elif 'one' in logic: n = 1
    elif 'two' in logic: n = 2
    elif 'three' in logic: n = 3
    elif 'four' in logic: n = 4
    elif 'five' in logic: n = 5
    elif 'six' in logic: n = 6
    elif 'seven' in logic: n = 7
    elif 'eight' in logic: n = 8
    elif 'nine' in logic: n = 9
    elif 'ten' in logic: n = 10
    else: 
        print(f'Invalid number for:\n{logic}')
        return res
    res.append((n, options))
    return res, options, additional
    

if __name__ == '__main__':
    # print(getTable2Courses(2023))
    # getAcademicPrograms()
    getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2023)
