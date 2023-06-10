from sqlite3 import OperationalError
from common.connect import connectDB


db = connectDB(None)
cursor = db.cursor()
cursor.execute('CREATE DATABASE IF NOT EXISTS LooScheduleDB')
print('Database schema created successfully for LooSchedule!')
db = connectDB()
cursor = db.cursor()
fd = open('createTables.sql', 'r')
sqlFile = fd.read()
fd.close()
sqlCommands = sqlFile.split(';')
for command in sqlCommands:
    try:
        cursor.execute(command)
    except OperationalError as msg:
        print("Command skipped: ", msg)
print('Tables created successfully for LooSchedule!')
