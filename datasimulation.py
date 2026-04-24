import json
import random
import pandas as pd
from datetime import datetime, timedelta

devices = 200

LAT_MIN, LAT_MAX = 12.80, 13.10
LON_MIN, LON_MAX = 80.10, 80.30

start_date = datetime(2026,1,1)
end_date = datetime(2026,1,31)

data = []

for d in range(devices):

    device_id = f"cane_{d}"

    # random number of trips
    trips = random.randint(0,10)

    for t in range(trips):

        trip_id = f"{device_id}_trip_{t}"

        # random trip start time within month
        trip_start = start_date + timedelta(
            seconds=random.randint(0,int((end_date-start_date).total_seconds()))
        )

        # trip duration (5–40 minutes)
        duration_minutes = random.randint(5,40)

        steps = int((duration_minutes*60)/5)

        start_lat = random.uniform(LAT_MIN, LAT_MAX)
        start_lon = random.uniform(LON_MIN, LON_MAX)

        end_lat = start_lat + random.uniform(-0.02,0.02)
        end_lon = start_lon + random.uniform(-0.02,0.02)

        lat_step = (end_lat-start_lat)/steps
        lon_step = (end_lon-start_lon)/steps

        lat = start_lat
        lon = start_lon

        for i in range(steps):

            timestamp = trip_start + timedelta(seconds=i*5)

            obstacle_flag = random.random() < 0.1
            fall_flag = random.random() < 0.003

            record = {
                "timestamp": str(timestamp),
                "device_id": device_id,
                "trip_id": trip_id,
                "Sensors": {
                    "ESP32": True,
                    "gps": {
                        "Nav_Flag": True,
                        "flag": obstacle_flag,
                        "latitude": str(lat),
                        "longitude": str(lon)
                    }
                },  
                "fall_detection": {
                    "Flag": str(fall_flag).lower()
                }
            }

            data.append(record)

            lat += lat_step
            lon += lon_step


with open("smart_cane_batch.json","w") as f:
    json.dump(data,f)

df = pd.DataFrame(data)
df.to_csv("smart_cane_batch.csv",index=False)

print("Total events:",len(data))