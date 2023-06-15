from requests import get
from bs4 import BeautifulSoup
from re import compile
from sqlite3 import OperationalError
from sqlalchemy import Column, String, ForeignKey
from datetime import datetime
from settings import SESSION, BASE, UndergradCalendarBaseURL, CSDegreeRequirementsURL


class Breath(BASE):
    __tablename__ = 'Breath'
    courseID = Column(String, primary_key=True, nullable=False) # ForeignKey('Course.courseID')
    subject = Column(String, nullable=False)
    code = Column(String, nullable=False)
    category=Column(String, nullable=False)

    def __init__(self, courseID, category):
        self.courseID = courseID
        self.subject, self.code = courseID.split()
        self.category = category


def getSubjects():
    html = get(CSDegreeRequirementsURL).text
    soup = BeautifulSoup(html, features='html.parser')
    breathAndDepth = soup.find(string=compile('Elective breadth requirements')).find_next_sibling()
    categories = breathAndDepth.find_all('li')
    subjects = []
    for i, category in enumerate(categories):
        start, end = (4, -7) if i < len(categories) - 1 else (10, -26)
        name = category.find(string=compile('unit')).split('unit')[1][start:end]
        for subject in category.find_all('a', href=compile('courses')):
            subjects.append((subject.get_text(), name))
    return subjects


def getBreathData(start=2019):
    subjects = getSubjects()
    today = datetime.today()
    courses = {}
    for year in range(start, today.year + 1):
        print(f'Collecting breath data for year {year}-{year + 1}!')
        index = str(year)[2:] + str(year + 1)[2:]
        for subject in subjects:
            url = UndergradCalendarBaseURL + index + '/COURSE/course-' + subject[0] + '.html'
            html = get(url).text
            soup = BeautifulSoup(html, features='html.parser')
            for course in soup.find_all('center'):
                courseID = course.find('a').attrs['name']
                courseID = courseID.replace(subject[0], subject[0] + ' ')
                courses[courseID] = Breath(courseID, subject[1])
    try:
        SESSION.add_all(courses.values())
        SESSION.commit()
        SESSION.close()
    except OperationalError as msg:
        print("Error: ", msg)


if __name__ == '__main__':
    getBreathData()
