import mysql.connector
from json import load
from sqlite3 import OperationalError


path = open('mysqlConfig.json')
config = load(path)
db = mysql.connector.connect(
    host=config['host'],
    user=config['user'],
    password=config['password'],
)
cursor = db.cursor()

cursor.execute('CREATE DATABASE IF NOT EXISTS LooScheduleDB')
print('Database schema created successfully for LooSchedule!')
db = mysql.connector.connect(
    host=config['host'],
    user=config['user'],
    password=config['password'],
    database='LooScheduleDB',
)
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
