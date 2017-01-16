#!/usr/bin/env sh

spark-submit --class logs.LogAnalyzerAppMain --master local[4] target/scala-2.11/stream-assembly-1.0.jar -w 3000 -s 1000 -l files/apache_logs -c files/checkpoint -o files/out
#spark-submit --class Stream target/scala-2.11/stream-assembly-1.0.jar localhost 7777 1
#spark-submit --class Stream target/scala-2.11/stream_2.11-1.0.jar localhost 7777 1
#spark-submit --class WordCount target/scala-2.11/stream_2.11-1.0.jar localhost 7777 3
