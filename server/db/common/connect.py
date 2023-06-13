import sqlite3
from json import load
import os


def connectDB(database='LooScheduleDB.db'):
    db = sqlite3.connect(
        database
    )
    return db