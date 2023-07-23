from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker,scoped_session
from sqlalchemy.ext.declarative import declarative_base
import os

# Change the .env file to your personal one
# config = dotenv_values(".env")
# please use your own path
pathToDB = "/Users/keqinyang/Desktop/CS446/LooSchedule/data/db/LooSchedule.db"
engine = create_engine(f'sqlite:///{pathToDB}')
Session = scoped_session(
    sessionmaker(bind=engine)
)

Base = declarative_base()