from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker,scoped_session
from sqlalchemy.ext.declarative import declarative_base
from dotenv import dotenv_values, load_dotenv
import os

load_dotenv()
# Change the .env file to your personal one
config = dotenv_values(".env")
pathToDB = os.environ['pathToDB']
engine = create_engine(f'sqlite:///{pathToDB}')
Session = scoped_session(
    sessionmaker(bind=engine)
)

Base = declarative_base()