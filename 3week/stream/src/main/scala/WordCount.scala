import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream

/**
  * Created by lumi.jang on 2017. 1. 10..
  */
object WordCount {

  def main(args: Array[String]) {
    if ( args.length < 3 ) {
      System.err.println("<host> <port> <batchInterval> <output filename>")
    }
    val host = args(0)
    val port = args(1).toInt
    val batchInterval = args(2).toInt
    println(s"exec with master=local[4], host=$host, port=$port, batchInterval=$batchInterval sec")

    val conf = new SparkConf().setMaster("local[4]").setAppName("wordcount-streaming")
    val ssc = new StreamingContext(conf, Seconds(batchInterval))
    val lines = ssc.socketTextStream(host, port)

    val words = lines.flatMap(_.split(" "))
    val filteredWords = dStreamFilter(words)

    words.print
    filteredWords.print
    filteredWords.saveAsTextFiles("results/filteredWord")

    println("==== start spark streaming context ====")
    ssc.start()
    println("==== await to terminate spark streaming context ====")
    ssc.awaitTerminationOrTimeout(10000)
    println("==== done! ====")
    ssc.stop()
  }

  def dStreamFilter(lines: DStream[String]) = {
    lines.filter(_.contains("a"))
  }
}
