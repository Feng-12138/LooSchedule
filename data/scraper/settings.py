from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker,scoped_session
from sqlalchemy.ext.declarative import declarative_base
import os

PATH = os.path.abspath(os.curdir).split('LooSchedule')[0] + 'LooSchedule/data/db/LooSchedule.db'
ENGINE = create_engine(f'sqlite:///{PATH}')
SESSION = scoped_session(sessionmaker(bind=ENGINE))
BASE = declarative_base()

UndergradCalendarBaseURL = 'https://ucalendar.uwaterloo.ca/'
CourseDescriptionsIndexURL='https://ugradcalendar.uwaterloo.ca/page/Course-Descriptions-Index'
MathDegreeRequirementsURL='https://ugradcalendar.uwaterloo.ca/page/MATH-Degree-Requirements-for-Math-students'
CSDegreeRequirementsURL='https://ugradcalendar.uwaterloo.ca/page/MATH-Bachelor-of-Computer-Science-1'
MathProgramListURL='https://ugradcalendar.uwaterloo.ca/page/MATH-List-of-Academic-Programs-or-Plans'
UWFlow = 'https://uwflow.com/graphql'
