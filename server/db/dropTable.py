from sqlite3 import OperationalError
from common.connect import connectDB

def dropTable():
    db = connectDB()
    cursor = db.cursor()
    with open("dropTable.sql", "r") as dropTable:
        while True:
            command = dropTable.readline()
            if not command:
                break
        try:
            cursor.execute(command)
        except OperationalError as msg:
            print("Command skipped: ", msg)
    if db:
        db.close()
        print("All tables drop successfully!")
        
if __name__ == "__main__":
    dropTable()