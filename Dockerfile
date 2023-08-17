FROM python:3.9

WORKDIR /data

# COPY data .

COPY ./data .

CMD ["rm", "./db/LooSchedule.db"]
CMD ["python3", "./test.py"]

