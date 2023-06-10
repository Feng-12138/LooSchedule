import mysql.connector
from json import load
import os


def connectDB(database='LooScheduleDB'):
    path = os.path.abspath(os.curdir)
    file = open(path.split('LooSchedule')[0] + 'LooSchedule/server/db/common/mysqlConfig.json')
    config = load(file)
    db = mysql.connector.connect(
        host=config['host'],
        user=config['user'],
        password=config['password'],
        database=database,
    )
    return db