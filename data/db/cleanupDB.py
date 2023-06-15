from sqlite3 import OperationalError
from common.connect import connectDB
from os import remove

def dropTables():
    db = connectDB()
    cursor = db.cursor()
    with open('sql/dropTables.sql', 'r') as file:
        while True:
            command = file.readline()
            if not command:
                break
            try:
                cursor.execute(command)
            except OperationalError as msg:
                print('Command skipped: ', msg)
    if db:
        cursor.close()
        db.close()
        print('Tables dropped successfully for LooSchedule!')
    remove('LooSchedule.db')
        
if __name__ == '__main__':
    dropTables()
