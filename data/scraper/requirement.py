from requests import get
from bs4 import BeautifulSoup
from re import compile, findall, search
from sqlite3 import OperationalError
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey
from datetime import datetime
from enum import Enum
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

ADDITIONAL_REQS = {'breadth and depth required', 'concentration required'}

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
            planUrl = parentTag.next_sibling.find('a', text='Degree Requirements')['href']
        if 'Overview and Degree Requirements' in planName:
            planName = planName.replace(' Overview and Degree Requirements', '')
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
    if name == 'Joint Computer Science (Bachelor of Mathematics)':
        html = get(UndergradCalendarBaseURL + '/page/MATH-Joint-Bachelor-of-Computer-Science-1' + param).text
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
    addReq, coopOnly, isDD = None, False, False
    for choice in choices:
        try:
            res, courses = updateRequirement(res, courses, choice, table2Courses)
        except Exception as msg:
            if str(msg) in ADDITIONAL_REQS: addReq = str(msg)
            else: raise msg
    courses = parseRequirement(res)
    if 'co-op only' in name.lower(): coopOnly = True
    if 'double degree' in name.lower(): isDD = True
    addRequirement(name, year, courses, addReq, url, coopOnly, isDD)
    

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
    logic = choice.contents[0].get_text().lower()
    if 'breadth and depth' in logic or '5.0 non-math units' in logic: 
        raise Exception('breadth and depth required')
    n, additional = 0, False
    options, res = [], []
    if 'additional' in logic: 
        additional = True
        logic = logic.replace('additional', '')
    if choice.find_all('li') == []:
        if 'concentration' in choice.get_text(): raise Exception('concentration required')
        if 'level' in logic:
            levels = []
            levels = findall(r'\b\d+\b', logic.split('level')[0])
            subjects = [a.get_text() for a in choice.find_all('a')]
            subjects = list(filter(lambda s: 'advisor' not in s, subjects))
            for subject in subjects:
                if len(levels) == 0: options.append(subject + ' xxx')
                for level in levels:
                    options.append(subject + ' ' + level[0] + 'xx')
        elif 'from' in logic:
            if choice.find('a') is None:
                options = choice.get_text().split('from ')[1].split(', ')
                if 'with at least' in logic:
                    logics = choice.get_text().split('with at least')
                    total, atLeast = stringToNum(logics[0].lower()), stringToNum(logics[1].lower())
                    atLeastOptions = [o.replace('.', '') for o in logics[1].split('from ')[1].split(', ')]
                    atLeastOptions = list(filter(lambda o: o != '', atLeastOptions))
                    restOptions = logics[0].split('from ')[1].split(', ')
                    restOptions = list(filter(lambda o: o != '', restOptions))
                    res.append((atLeast, atLeastOptions))
                    res.append((total - atLeast, restOptions))
                    options = set(atLeastOptions + restOptions)
                    return res, options, additional  
            else:
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
            logic, totalUnits = '', 0
            for c in choice.contents: logic += str(c)
            if additional: logic = logic.replace('additional', '')
            subjects = [a.get_text() for a in choice.find_all('a')]
            if stringToNum(logic.lower()): 
                total = stringToNum(logic.lower())
                if len(subjects) == 0: 
                    res.append((total, {'xx xxx'}))
                    return res, options, additional
                totalUnits = float(total * 2)
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
        elif len(choice.find_all('a')) == 1:
            course = choice.find('a').get_text()
            res.append((1, course))
            options.append(course)
            return res, options, additional
    elif 'excluding' in choice.get_text():
        levels = []
        if 'level' in logic:
            levels = findall(r'\b\d+\b', logic.split('level')[0])
        subjects = [a.get_text() for a in choice.find_all('a')]
        subjects = list(filter(lambda s: not any(c.isdigit() for c in s), subjects))
        for subject in subjects:
            if len(levels) == 0: options.append(subject + ' xxx')
            for level in levels:
                options.append(subject + ' ' + level[0] + 'xx')
    else:
        for course in choice.find_all('li'):
            if 'level' in course.get_text():
                levels = findall(r'\b\d+\b', course.get_text().split('level')[0])
                subjects = [a.get_text() for a in course.find_all('a')]
                if 'Note:' in course.contents[0].get_text(): continue
                elif 'Note:' in course.get_text():
                    start = search(r'\W+', course.get_text()).start()
                    subjects = [course.get_text()[:start]]
                elif len(subjects) == 0:
                    start = search(r'\W+', course.get_text()).start()
                    subjects = [course.get_text()[:start]]
                for subject in subjects:
                    if len(levels) == 0: options.append(subject + ' xxx')
                    for level in levels:
                        options.append(subject + ' ' + level[0] + 'xx')
            elif course.find('a') is None:
                if 'BUS' in course.get_text(): continue
                if 'from' in course.get_text():
                    options += course.get_text().split('from ')[1].split(', ')
                else: options += [course.get_text()]
            else:
                options += [a.get_text() for a in course.find_all('a')]
    if 'all' in logic:
        if choice.find('li') is None:
            if choice.find_next_sibling() and choice.find_next_sibling().name == 'ul':
                for course in choice.find_next_sibling().find_all('li'):
                    option = [a.get_text() for a in course.find_all('a')]
                    res.append((1, set(option)))
        else:
            for course in choice.find_all('li'):
                option = [a.get_text() for a in course.find_all('a')]
                res.append((1, set(option)))
        return res, options, additional
    elif stringToNum(logic): n = stringToNum(logic)
    elif 'unit' in logic:
        totalUnits = float(findall(r'\b\d+\b', logic.split('unit')[0])[0])
        atLeastUnits, levels = 0, []
        courses = []
        if 'at least' in logic:
            atLeastUnits = float(findall(r'\b\d+\b', logic.split('at least')[1].split('unit')[0])[0])
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


def stringToNum(s):
    if 'one' in s: return 1
    elif 'two' in s: return 2
    elif 'three' in s: return 3
    elif 'four' in s: return 4
    elif 'five' in s: return 5
    elif 'six' in s: return 6
    elif 'seven' in s: return 7
    elif 'eight' in s: return 8
    elif 'nine' in s: return 9
    elif 'ten' in s: return 10
    return 0


def parseRequirement(requirement):
    print(requirement)
    print()
    res = ''
    for r in requirement:
        res += str(r[0]) + ':'
        for option in sorted(list(r[1])):
            res += option + ','
        res = res[:-1] + ';'
    return res[:-1]


def addRequirement(planName, year, courses, addReq, link, coopOnly, isDD):
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
    getProgramRequirements('Computer Science', '/group/MATH-Computer-Science-1', 2023)
    getProgramRequirements('Computing and Financial Management', '/group/MATH-Computing-and-Financial-Management', 2023)
    getProgramRequirements('Mathematics/Business', '/group/MATH-Mathematics-or-Business', 2023)
    getProgramRequirements('Mathematical Optimization', '/group/MATH-Mathematical-Optimization1', 2023)
    getProgramRequirements('Mathematics/Teaching', '/group/MATH-Mathematics-or-Teaching', 2023)
    getProgramRequirements('Pure Mathematics', '/group/MATH-Pure-Mathematics-1', 2023)
    getProgramRequirements('Statistics', '/group/MATH-Statistics-1', 2023)
