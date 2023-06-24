import sqlite3
import os

def connectDB():
    path = os.path.abspath(os.curdir).split('LooSchedule')[0] + 'LooSchedule/data/db/LooSchedule.db'
    db = sqlite3.connect(path)
    return db
