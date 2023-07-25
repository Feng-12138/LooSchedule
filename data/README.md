# Database Setup
## Create the Database and Tables

Navigate to the database directory located at `LooSchedule\data\db`:
```
cd db
```

Create the database schema and tables. A file named `LooSchedule.db` should appear in the directory after running the following command:
```
python3 -B setupDB.py
```

To drop the database, run the following command:
```
python3 -B cleanupDB.py
```


## Run Scrapers to Retrieve and Populate Data

Navigae to the scraper directory located at `LooSchedule\data\scraper`:
```
cd scraper
```

Add course data:
```
TODO
```

Add communication data:
```
python3 -B communication.py
```

Add breadth data:
```
python3 -B breadth.py
```

Add requirement data (major, minor, specialization, joint, etc.) [TEMPORARY]:
```
python3 -B requirement.py
```
Add course data:
```
python3 -B courses/getCourseFromUWflow.py
```
Note that if fetch fail, due to url error, try couple times more(I could write the own version of get, but just too lazy)
