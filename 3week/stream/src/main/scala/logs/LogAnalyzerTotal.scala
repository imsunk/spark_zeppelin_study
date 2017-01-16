package logs;

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream._

/**
 * Compute totals on the log input
 */
object LogAnalyzerTotal {

  def computeRunningSum(values: Seq[Long], state: Option[Long]) = {
    Some(values.reduce((x, y) => x + y) + state.getOrElse(0L))
  }

  def processAccessLogs(accessLogsDStream: DStream[ApacheAccessLog]) {
    val ipDStream = accessLogsDStream.map(entry => (entry.getIpAddress(), 1))
    val ipCountsDStream = ipDStream.reduceByKey((x, y) => x + y)
    ipCountsDStream.print()

    val ipRawDStream = accessLogsDStream.transform{
      rdd => rdd.map(accessLog => (accessLog.getIpAddress(), 1)).reduceByKey(
        (x, y) => x +y)
    }
    ipRawDStream.print()

    val ipBytesDStream = accessLogsDStream.map(entry => (entry.getIpAddress(), entry.getContentSize()))
    val ipBytesSumDStream = ipBytesDStream.reduceByKey((x, y) => x + y)
    val ipBytesRequestCountDStream = ipRawDStream.join(ipBytesSumDStream)
    ipBytesRequestCountDStream.print()

    val responseCodeDStream = accessLogsDStream.map(log => (log.getResponseCode(), 1L))
    val responseCodeCountDStream = responseCodeDStream.updateStateByKey(computeRunningSum _)
  }
}
