# spark streaming

SparkContext sc
sc.textFile() = RDD
  
  - RDD.transformationFunc() = transformed RDD
  - RDD.action()

StreamingContext ssc
ssc.socketTextStream() = DStream 
ssc.textFileStream() = DStream
KafkaUtils.createStream(ssc, ...) = DStream
FlumUtils.createStream(ssc, ...) = DStream

  - stateless transformation
  DStream.transformationFunc() = transformed DStream 
  ~= RDD transformation
  http://spark.apache.org/docs/latest/streaming-programming-guide.html#transformations-on-dstreams
  
  join (stream+steam, stream+dataset(RDD))
  http://spark.apache.org/docs/latest/streaming-programming-guide.html#join-operations

  - stateful transformation

  updateStateByKey
  http://spark.apache.org/docs/latest/streaming-programming-guide.html#updatestatebykey-operation

  windowOperations
  DStream.window(window duration, sliding duration)
  http://spark.apache.org/docs/latest/streaming-programming-guide.html#window-operations

  - DStream.outputOperation()
  http://spark.apache.org/docs/latest/streaming-programming-guide.html#output-operations-on-dstreams

# for test

nc (netcat) => open socket
server $ nc -lk 7777
client $ telnet localhost 7777
=> simple chat server

fake-apache-log-generator
https://github.com/kiritbasu/Fake-Apache-Log-Generator
$ pip install -r requirements.txt
$ python apache-fake-log-gen.py -n 0 -o LOG 

# sbt assembly
https://github.com/sbt/sbt-assembly
project-root-dir/project/build.properties => check sbt version
