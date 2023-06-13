from sqlite3 import OperationalError
from common.connect import connectDB


# db = connectDB()
# cursor = db.cursor()
# cursor.execute('CREATE DATABASE IF NOT EXISTS LooScheduleDB')
# print('Database schema created successfully for LooSchedule!')
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
if db:
    db.close()
    print('Tables created successfully for LooSchedule!')
