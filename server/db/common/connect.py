import mysql.connector
import sqlite3
from json import load
import os
import time


def connectDB(database='LooScheduleDB.db'):
    path = os.path.abspath(os.curdir)
    file = open(path.split('LooSchedule')[0] + 'LooSchedule/server/db/common/mysqlConfig.json')
    config = load(file)
    db = sqlite3.connect(
        # host=config['host'],
        # user=config['user'],
        # password=config['password'],
        # database=database,
        database
    )
    return db