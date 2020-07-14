package Dockerize_SBT_Project.src.main.scala

import Dockerize_SBT_Project.src.main.scala.config._
import Dockerize_SBT_Project.src.main.scala.data.DataSource
import Dockerize_SBT_Project.src.main.scala.King.CrawlerKing

import akka.actor.{ActorSystem, Props}
import akka.event.Logging


/*
Akka structure:
https://medium.com/@yuriigorbylov/akka-ask-antipattern-8361e9698b20#:~:text=What%20is%20ask%20pattern%3F,should%20fire%20a%20message%20back.
 */


/*
Need to make sure that th target topic in Kafka server has been existed
 */


/***
 * Decentralized crawler main logical code
 */
object decentralize_crawler extends App {

  // Get and analyse data
  val stockJsonData = new DataSource

  /*
  https://stackoverflow.com/questions/44531937/convert-a-row-to-a-list-in-spark-scala
   */
  val companiesData = stockJsonData.companyData().rdd.map(_.toSeq.toList).collect().toList.flatten
  val companyStockSymbols = stockJsonData.stockSymbolData().rdd.map(_.toSeq.toList).collect().toList.flatten
  val testData = stockJsonData.stockSymbolData().head(2).map(_.toSeq.toList).toList.flatten

  println("=+=+=+=+=+=+=+=+=+=+=+=+= All Taiwan listed company: =+=+=+=+=+=+=+=+=+=+=+=+=")
  println(companiesData)
  println("=+=+=+=+=+=+=+=+=+= All Taiwan listed company stock symbol: =+=+=+=+=+=+=+=+=+=")
  println(companyStockSymbols)
  println("=+=+=+=+=+=+=+=+=+=+=+=+= The data which for testing: =+=+=+=+=+=+=+=+=+=+=+=+=")
  println(testData)

  stockJsonData.close_spark()

  // Multiple implicit value
  // https://stackoverflow.com/questions/38863424/scala-multiple-implicit-parameters-with-defaults-resulting-in-ambiguous-values
//  val crawlerRepository = new CrawlerAPIsRepository
//  implicit val topic = "target-crawl-APIs"
//  testData.foreach(api => crawlerRepository.kafkaMsg("key")(api.toString))

  // Build Master actor.
  val system = ActorSystem(AkkaConfig.SystemName)
  val allKingName = AkkaConfig.AllKingName
  val king = system.actorOf(Props[CrawlerKing], s"$allKingName")

  // Build Actor Logging mechanism
  val actorLog = Logging(system.eventStream, "king.log.stream")
  actorLog.info(s"The crawler King actor (a.k.a Master Actor) name is $allKingName.")
  // Send tasks to the master actor.
  king ! StockDataRequirement("I want all listed company stock data in Taiwan.", testData.length.toFloat, testData)

  actorLog.info("Program Finish!")
}
