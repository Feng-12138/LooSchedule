from db.cleanupDB import dropTables
from db.setupDB import createTables
from scraper.breadth import getBreadthData
from scraper.communication import getAllCommunicationData
from scraper.requirement import getAcademicPrograms
from scraper.courses.getCourseFromUWflow import getCourseFromUWflow
import sys


def 