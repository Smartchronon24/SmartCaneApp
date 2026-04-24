
from pyspark.sql import SparkSession
from pyspark.sql.functions import col
from pyspark.sql.functions import to_timestamp
from pyspark.sql.functions import min, max, unix_timestamp, col
from pyspark.sql.functions import round
from pyspark.sql.functions import hour


spark = SparkSession.builder.appName("SmartCaneAnalytics").getOrCreate()

df = spark.read.json("smart_cane_batch.json")

df.printSchema()
df.show(10)


flat_df = df.select(
    "timestamp",
    "device_id",
    "trip_id",
    col("Sensors.gps.latitude").alias("latitude"),
    col("Sensors.gps.longitude").alias("longitude"),
    col("Sensors.gps.flag").alias("obstacle"),
    col("fall_detection.Flag").alias("fall")
)

flat_df.show(5)

flat_df = flat_df.withColumn(
    "timestamp",
    to_timestamp("timestamp")
)

flat_df.show(5)

usage = flat_df.groupBy("device_id") \
    .count().orderBy("count", ascending=False)

usage.show()



trip_duration = flat_df.groupBy("trip_id").agg(
        min("timestamp").alias("start_time"),
        max("timestamp").alias("end_time")
    ).withColumn(
        "trip_duration_Minutes",
        (unix_timestamp(col("end_time")) - unix_timestamp(col("start_time")))/60
    ).orderBy("trip_id")

trip_duration.show()



danger = flat_df.filter(col("obstacle") == True).groupBy(
        round("latitude",3).alias("lat"),
        round("longitude",3).alias("lon")
    ).count().orderBy("count", ascending=False)

danger.show()

falls = flat_df.filter(col("fall") == "true").groupBy(
        round("latitude",3),
        round("longitude",3)
    ).count().orderBy("count", ascending=False)

falls.show()

visits = flat_df.groupBy(
    round("latitude",3),
    round("longitude",3)
).count().orderBy("count", ascending=False)

visits.show()

hourly = flat_df \
    .withColumn("hour", hour("timestamp")) \
    .groupBy("hour") \
    .count() \
    .orderBy("hour")

hourly.show()