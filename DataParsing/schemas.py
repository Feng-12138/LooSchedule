from sqlalchemy import Column, Integer, String, Boolean, DECIMAL, ForeignKey
from __init__ import Base

class Course(Base):
    __tablename__ = "Course"
    courseID = Column(String, primary_key=True, nullable=False)
    courseName = Column(String, nullable=False)
    subject = Column(String, nullable=False)
    code = Column(String, nullable=False)
    description = Column(String, nullable=False)
    credit = Column(DECIMAL, nullable=False)
    availability = Column(String)
    OnlineTerms = Column(String)
    coreqs = Column(String)
    antireqs = Column(String)
    likedRating = Column(DECIMAL)
    easyRating = Column(DECIMAL)
    usefulRating = Column(DECIMAL)
    

        
class Prerequisite(Base):
    __tablename__ = "Prerequisite"
    courseID = Column(String, ForeignKey('Course.courseID'), primary_key=True, nullable=False)
    consentRequired = Column(Boolean, nullable=False)
    courses = Column(String)
    minimumLevel = Column(String)
    onlyOpenTo = Column(String)
    notOpenTo = Column(String)