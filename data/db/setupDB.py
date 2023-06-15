from sqlite3 import OperationalError
from common.connect import connectDB

def createTables():
    db = connectDB()
    cursor = db.cursor()
    fd = open('sql/createTables.sql', 'r')
    sqlFile = fd.read()
    fd.close()
    sqlCommands = sqlFile.split(';')
    for command in sqlCommands:
        try:
            cursor.execute(command)
        except OperationalError as msg:
            print("Command skipped: ", msg)
    if db:
        cursor.close()
        db.close()
        print('Tables created successfully for LooSchedule!')

if __name__ == '__main__':
    createTables()
