#!/bin/sh
spark-submit --master spark://172.17.138.67:7077 --name StaticWindowLoadTime --class com.unionpay.ubass.spark.StaticWindowLoadTime --executor-memory 1G --total-executor-cores 3 --jars spark-streaming-flume_2.10-1.2.1.jar,flume-ng-sdk-1.4.0.jar spark.jar 0.0.0.0 33333 60000
spark-submit --master yarn --name StaticWindowLoadTime --class com.unionpay.ubass.spark.StaticWindowLoadTime --executor-memory 1G --total-executor-cores 3 --jars spark-streaming-flume_2.10-1.2.1.jar,flume-ng-sdk-1.4.0.jar spark.jar 0.0.0.0 33333 20000

