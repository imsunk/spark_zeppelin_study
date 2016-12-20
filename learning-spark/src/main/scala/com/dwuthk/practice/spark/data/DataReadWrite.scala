package com.dwuthk.practice.spark.data

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.dwuthk.practice.spark.entity.Game
import java.io.StringWriter
import au.com.bytecode.opencsv.CSVWriter

object DataReadWrite {

  val logger = LoggerFactory.getLogger("JsonData");
  val mapper = new ObjectMapper with ScalaObjectMapper();
  
  def main(args: Array[String]): Unit = {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.registerModule(DefaultScalaModule)
    
    val conf = new SparkConf().setAppName("Test App").setMaster("local");
    conf.set("spark.hadoop.validateOutputSpecs", "false");
    val sc = new SparkContext(conf);

    // Text File Load
    val input = sc.textFile("resources/games.json");
    
    logger.info("## Json String Count : {}", input.count());
    logger.info("## Json String Take 2 : {}", input.take(2));

    // Json String -> Object
    val games = input.mapPartitions(records => { 
      records.flatMap(record => {
        try {
          Some(mapper.readValue(record, classOf[Game]))
        } catch {
          case e: Exception => None
        }
      })
    }, true)

    logger.info("## Object Count : {}", games.count());
    logger.info("## Object Take 2 : {}", games.take(2));

    // Filter -> Contains "Mario"
    val marioGames = games.filter { x => x.name.contains("Mario") }
    
    // Output Mario To Json String
    val jsonMario = marioGames.mapPartitions(records => {
      records.map { x => mapper.writeValueAsString(x) }
    }, true);
    jsonMario.saveAsTextFile("marioJSon");
    
    
    // Sort By Score. Mario
    val sorted = marioGames.sortBy(mario => mario.score, false);
    
    // Output Sorted Mario To Json String
    val sortedJson = sorted.mapPartitions(records => {
      records.map { x => mapper.writeValueAsString(x) }
    }, true)
    sortedJson.saveAsTextFile("sortedMarioJson");
    
    
    sorted.saveAsObjectFile("object");
    
    // Spark SQL
    // Spark 2.0 이상부터는 책의 예제와는 다른 API 를 사용. (예제 API 는 Deprecated)
    val sqlContext = new SQLContext(sc);
    val spark = SparkSession.builder().appName("Test App").enableHiveSupport().getOrCreate();
    
    
    import sqlContext.implicits._
    
    //sqlContext.

    // Spark View (Table)
    val dataFrame = marioGames.toDF() //spark.createDataFrame(mario, classOf[Game]);
    dataFrame.createOrReplaceTempView("games");
    
    logger.info("## Mario Games Count : {}", marioGames.count());
    
    val superMarios = dataFrame.sqlContext.sql("SELECT * FROM games WHERE name like '%Super%'");
    
    logger.info("## Super Mario Games Count : {}", superMarios.count());
    
    logger.info("## SELECT COUNT FROM GAME SCEMA : {}", spark.sql("SELECT count(*) FROM games").collect());


  }
}