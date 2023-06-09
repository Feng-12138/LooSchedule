from requests import get
from bs4 import BeautifulSoup
from re import compile, findall
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

    def __init__(self, requirementID, majorName, coopOnly=False, isDoubleDegree=False):
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


requirementIDCounter = 1


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
    for a in aList:
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
    # print(contents)
    aList = [a.get_text() for a in contents.find_all('a')]
    res, courses = [], set()
    table2Courses = []
    if 'Table 2' in aList: 
        res, courses = getTable2Courses(year)
        table2Courses = courses
    choices = contents.find_next('ul').contents
    choices = list(filter(lambda c: c != '\n', choices))
    # print(choices)
    for choice in choices:
        res, courses = updateRequirement(res, courses, choice, table2Courses)
    courses, addReq = parseRequirement(res)
    addRequirement(name, year, courses, addReq, url)
    

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


def getPlanType(planName):
    name = planName.lower()
    if 'minor' in name: return 'minor'
    elif 'specialization' in name: return 'specialization'
    elif 'joint' in name: return 'joint'
    else: return 'major'


def updateRequirement(requirement, courses, choice, table2Courses):
    r, c, a = parseChoice(choice)
    if a:
        requirement += r
        courses = courses.union(c)
        return requirement, courses
    for (n, options) in r:
        duplicates = set()
        for option in options:
            if option in table2Courses:
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
                    requirement.append((m, req[1].intersection(duplicates)))
                    if n - req[0] != 0:
                        reduced.append((abs(n - req[0]), req[1].symmetric_difference(duplicates)))
            requirement += reduced
        else:
            requirement.append((n, options))
            courses = courses.union(options)
    return requirement, courses


def parseChoice(choice):
    if choice.name == 'ul': return [], set(), False
    logic = choice.contents[0].lower()
    n, additional = 0, False
    options, res = [], []
    if 'additional' in logic: 
        additional = True
        logic = logic.replace('additional', '')
    if choice.find_all('li') == []:
        # print(choice, logic)
        if 'concentration' in logic: return [], [], False # TODO:
        if 'level' in logic:
            levels = []
            levels = findall(r'\b\d+\b', logic.split('level')[0])
            subjects = [a.get_text() for a in choice.find_all('a')]
            for subject in subjects:
                if len(levels) == 0: options.append(subject + ' xxx')
                for level in levels:
                    options.append(subject + ' ' + level[0] + 'xx')
        elif 'from' in logic:
            # TODO:
            # print(choice.contents[0].split('from')[1])
            # print(choice.find_all('a'))
            courses = [a.get_text() for a in choice.find_all('a')]
            for course in courses:
                if any(char.isdigit() for char in course): options.append(course)
                else: options.append(course + ' xxx')
        elif choice.find_next_sibling() and choice.find_next_sibling().name == 'ul':
            for course in choice.find_next_sibling().find_all('li'):
                if 'level' in course.get_text():
                    levels = findall(r'\b\d+\b', course.get_text().split('level')[0])
                    subjects = [a.get_text() for a in course.find_all('a')]
                    for subject in subjects:
                        if len(levels) == 0: options.append(subject + ' xxx')
                        for level in levels:
                            options.append(subject + ' ' + level[0] + 'xx')
                else:
                    options.append(course.find('a').get_text())
        elif 'unit' in logic:
            logic = ''
            for c in choice.contents: logic += str(c)
            if additional: logic = logic.replace('additional', '')
            subjects = [a.get_text() for a in choice.find_all('a')]
            totalUnits = float(logic.split('unit')[0])
            atLeastUnits, levels = 0, []
            courses = []
            if 'at least' in logic:
                atLeastUnits = float(logic.split('at least')[1].split('unit')[0])
                levels = findall(r'\b\d+\b', logic.split('at least')[1].split('unit')[1].split('level')[0])
                totalUnits -= atLeastUnits
                for level in levels:
                    for subject in subjects:
                        options.append(subject + ' ' + level[0] + 'xx')
                        courses.append(subject + ' ' + level[0] + 'xx')
                res.append((int(atLeastUnits * 2), set(options)))
                options = []
            for subject in subjects: 
                options.append(subject + ' xxx')
                courses.append(subject + ' xxx')
            res.append((int(totalUnits * 2), set(options)))
            return res, courses, additional
    else:
        for course in choice.find_all('li'):
            if 'level' in course.get_text():
                levels = findall(r'\b\d+\b', course.get_text().split('level')[0])
                subjects = [a.get_text() for a in course.find_all('a')]
                for subject in subjects:
                    if len(levels) == 0: options.append(subject + ' xxx')
                    for level in levels:
                        options.append(subject + ' ' + level[0] + 'xx')
            # elif 'any' in course.get_text().lower():
            #     subjects = [a.get_text() for a in course.find_all('a')]
            #     for subject in subjects: options.append(subject + ' xxx')
            #     print('options',course,options)
            else:
                options.append(course.find('a').get_text())
    if 'all' in logic:
        for option in options: res.append((1, set([option])))
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
    elif 'unit' in logic:
        totalUnits = float(logic.split('unit')[0])
        atLeastUnits, levels = 0, []
        courses = []
        if 'at least' in logic:
            atLeastUnits = float(logic.split('at least')[1].split('unit')[0])
            levels = findall(r'\b\d+\b', logic.split('at least')[1].split('unit')[1].split('level')[0])
            subjects, satisfiedCourses = [], []
            for option in options:
                if any(char.isdigit() for char in option):
                    code = int(findall(r'\b\d+\b', option)[0])
                    if code >= min([int(level) for level in levels]):
                        satisfiedCourses.append(option)
                    courses.append(option)
                else: subjects.append(option)
            for level in levels:
                for subject in subjects:
                    satisfiedCourses.append(subject + ' ' + level[0] + 'xx')
                    courses.append(subject + ' ' + level[0] + 'xx')
            res.append((int(atLeastUnits * 2), set(satisfiedCourses)))
            additionalCourses = set(courses).difference(set(satisfiedCourses))
            for subject in subjects:
                additionalCourses.add(subject + ' xxx')
            res.append((int(totalUnits - atLeastUnits) * 2, additionalCourses))
            return res, courses, additional
        n = int(totalUnits * 2)
    else:
        print(f'Invalid number for:\n{choice.contents}')
        return res, options, additional
    res.append((n, set(options)))
    return res, options, additional


def parseRequirement(requirement):
    print(requirement)
    res = ''
    additionalReq = ''
    for r in requirement:
        res += str(r[0]) + ':'
        for option in sorted(list(r[1])):
            res += option + ','
        res = res[:-1] + ';'
    return res[:-1], additionalReq


def addRequirement(planName, year, courses, addReq, link, coopOnly=False, isDD=False):
    global requirementIDCounter
    schoolYear = str(year) + '-' + str(year + 1)
    r = Requirement(requirementIDCounter, getPlanType(planName), schoolYear, courses, addReq, UndergradCalendarBaseURL + link)
    requirementIDCounter += 1
    p = None
    if r.type == 'major':
        p = Major(r.requirementID, planName, coopOnly, isDD)
    elif r.type == 'minor':
        p = Minor(r.requirementID, planName)
    elif r.type == 'specialization':
        p = Specialization(r.requirementID, planName)
    elif r.type == 'joint':
        p = Joint(r.requirementID, planName)
    else:
        print('Invalid requirement type: ' + r.type + '!')
        return
    try:
        SESSION.add(r)
        SESSION.add(p)
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)

if __name__ == '__main__':
    # print(getTable2Courses(2023))
    # getAcademicPrograms()
    # getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2019)
    # getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2020)
    # getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2021)
    # getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2022)
    getProgramRequirements('Actuarial Science', '/group/MATH-Actuarial-Science-1', 2023)
    getProgramRequirements('Applied Mathematics', '/group/MATH-Applied-Mathematics-1', 2023)
    getProgramRequirements('Combinatorics and Optimization', '/group/MATH-Combinatorics-and-Optimization1', 2023)
    getProgramRequirements('Computational Mathematics', '/MATH-Computational-Mathematics-1', 2023)
    # getProgramRequirements('Computer Science', '/group/MATH-Computer-Science-1', 2023)
