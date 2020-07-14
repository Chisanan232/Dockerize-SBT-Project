package Dockerize_SBT_Project.src.main.scala.King

import Dockerize_SBT_Project.src.main.scala.config._
import Dockerize_SBT_Project.src.main.scala.Leader.Paladin

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.util.matching.Regex
import scala.concurrent.Await

import akka.actor.{Actor, ActorLogging, ActorPath, ActorPathExtractor, ActorPaths, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.ask


class CrawlerKing extends Actor with ActorLogging{

  val crawlerPaladinNumber: Int = AkkaConfig.CrawlerNumber
  var finishTasks = 0
  var allTasksAmount = 0

//  def receive: Receive = {
//
//    case StockDataRequirement =>
//      log.info("")
//
//        case StockDataRequirement =>
//
//          // 1. Send the requirement to Consumer Leader to get the target data
//          // 2. Receive the data which be send by consumer worker
//          // 3. Use the target data to crawl data.
//          // 4. Save data to DataBase 'Cassandra'
//
//          val consumerLeader = context.actorSelection(AkkaConfig.DataAnalyserDepartment.ConsumerLeaderPath)
//          consumerLeader ! NeedCrawlerData
//
//  }

//  val actorLog = Logging(context.system, this)

  def receive: Receive = {

    case StockDataRequirement(content, taskAmount, taskCondition) =>

      // 1. Send the requirement to Consumer Leader to get the target data
      // 2. Receive the data which be send by consumer worker
      // 3. Use the target data to crawl data.
      // 4. Save data to DataBase 'Cassandra'

      val consumerLeader = context.actorSelection(AkkaConfig.DataAnalyserDepartment.ConsumerLeaderPath)
      consumerLeader ! NeedCrawlerData

      log.info(s"The task amount is: $taskAmount")
      log.info(s"The task conditions we need to use is $taskCondition")

      // Analyse task amount and condition.
      // Analyse task conditions and distribute to each managers
      if (taskAmount.toInt < crawlerPaladinNumber) log.warning("The tasks amount is less than crawler paladins amount.")
      this.allTasksAmount = taskAmount.toInt
      val taskNumber = taskAmount.toInt / crawlerPaladinNumber
      val eachPaladinTasks = taskCondition.grouped(taskNumber.toInt).toList
      if (eachPaladinTasks.length != crawlerPaladinNumber) {
        log.error("The task distribute number is incorrect.")
      }

      // Build managers
      val crawlerPaladins = new Array[ActorRef](crawlerPaladinNumber)
      val managerActorName = AkkaConfig.CrawlerDepartment.crawlerPaladinName
      for (id <- 0 until crawlerPaladinNumber.toInt) {
        crawlerPaladins(id) = context.actorOf(Props[Paladin], s"$managerActorName" + s"_$id")
      }

//      val managerActorNamePattern: Regex = "crawler_Paladin.[0-9]{1,5}".r
      // Set a variable in regular expression:
      // https://stackoverflow.com/questions/28927533/scala-how-to-construct-a-regex-including-a-variable
      val managerActorNamePattern = Regex.quote(managerActorName) + ".[0-9]{1,5}" r
      var allChildActorsName = new ListBuffer[Any]()
      context.children.toList.foreach(name => {
        val result = managerActorNamePattern.findFirstIn(name.toString())
        if (result != None) {
          allChildActorsName += result.get.toString
        }
      })

      implicit val timeout = Timeout(5.seconds)
      crawlerPaladins.foreach(paladinRef => {
        val paladinResp = paladinRef ? PostingSignal("Hell, every paladins, my promises.")
        val result = Await.result(paladinResp, timeout.duration)
        log.info("Response from Paladin: " + result)

        val checksumManager = managerActorNamePattern.findFirstIn(result.toString)
        if (checksumManager != None) {
          log.info("[King] Thank you Paladin " + checksumManager.get.toString)

          val crawlTaskList = eachPaladinTasks.apply(paladinRef.path.name.takeRight(1).toInt)
          val paladin = context.actorSelection(paladinRef.path)
          paladin ! TaskConditions("This is your task parameters.", taskNumber, crawlTaskList)
//          paladin.tell(TaskConditions("This is your task parameters.", taskNumber, crawlTaskList))
          log.info("Send task to worker from managers.")
        } else {
          log.warning(" ")
        }
      })

    case g: GotData =>
      log.info("[King] Good job, my man !")
      finishTasks += 1
      if (finishTasks == this.allTasksAmount) {
        log.info("* [FINISH] Done this project.")
        context.system.terminate()
      }

  }

}

