from datetime import datetime
import sys
sys.path.append('../../')
from db.common.connect import connectDB
from communication import getCommunication


db = connectDB()
today = datetime.today()
curYear = today.year if today > datetime(today.year, 9, 1) else today.year - 1
for year in range(2019, curYear + 1):
    for course in getCommunication(year): course.insertDB(db)
db.commit()
