package logs;

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream._

/**
 * The LogAnalyzerAppMain is an sample logs analysis application.  For now,
 * it is a simple minimal viable product:
 *   - Read in new log files from a directory and input those new files into streaming.
 *   - Computes stats for all of time as well as the last time interval based on those logs.
 *   - Write the calculated stats to an txt file on the local file system
 *     that gets refreshed every time interval.
 *
 * Once you get this program up and running, feed apache access log files
 * into the local directory of your choosing.
 *
 * Then open your output text file, perhaps in a web browser, and refresh
 * that page to see more stats come in.
 *
 * Modify the command line flags to the values of your choosing.
 * Notice how they come after you specify the jar when using spark-submit.
 *
 * Example command to run:
 * %  ${YOUR_SPARK_HOME}/bin/spark-submit
 *     --class "com.oreilly.learningsparkexamples.scala.logs.LogAnalyzerAppMain"
 *     --master local[4]
 *     target/uber-log-analyzer-1.0.jar
 *     --logs_directory /tmp/logs
 *     --output_html_file /tmp/log_stats.html
 *     --index_html_template ./src/main/resources/index.html.template
 */
case class Config(WindowLength: Int = 3000, SlideInterval: Int = 1000,
                  LogsDirectory: String = "files/apache_logs",
                  CheckpointDirectory: String = "files/checkpoint",
                  OutputHTMLFile: String = "files/log_stats.html",
                  OutputDirectory: String = "files/out",
                  IndexHTMLTemplate :String ="./src/main/resources/index.html.template") {
  def getWindowDuration() = {
    new Duration(WindowLength)
  }
  def getSlideDuration() = {
    new Duration(SlideInterval)
  }
}

object LogAnalyzerAppMain {

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Config]("LogAnalyzerAppMain") {
      head("LogAnalyzer", "0.1")
      opt[Int]('w', "window_length") text("size of the window as an integer in miliseconds")
      opt[Int]('s', "slide_interval") text("size of the slide inteval as an integer in miliseconds")
      opt[String]('l', "logs_directory") text("location of the logs directory. if you don't have any logs use the fakelogs_dir script.")
      opt[String]('c', "checkpoint_directory") text("location of the checkpoint directory.")
      opt[String]('o', "output_directory") text("location of the output directory.")
    }
    val opts = parser.parse(args, new Config()).get
    val conf = new SparkConf().setMaster("local[4]").setAppName("A Databricks Reference Application: Logs Analysis with Spark");
    val ssc = new StreamingContext(conf, opts.getSlideDuration())

    // Checkpointing must be enabled to use the updateStateByKey function & windowed operations.
    ssc.checkpoint(opts.CheckpointDirectory)

    val logData = ssc.textFileStream(opts.LogsDirectory);
    val accessLogDStream = logData.map(line => ApacheAccessLog.parseFromLogLine(line)).cache()

    accessLogDStream.print

    LogAnalyzerTotal.processAccessLogs(accessLogDStream)
    LogAnalyzerWindowed.processAccessLogs(accessLogDStream, opts)

    println("==== start spark streaming context ====")
    ssc.start()
    println("==== await to terminate spark streaming context ====")
    ssc.awaitTerminationOrTimeout(9000)
    println("==== done! ====")
    ssc.stop()
  }
}
