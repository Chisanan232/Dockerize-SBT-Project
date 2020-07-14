package Dockerize_SBT_Project.src.main.scala.Leader

import Dockerize_SBT_Project.src.main.scala.config._
import Dockerize_SBT_Project.src.main.scala.Worker.Soldier

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.ActorLogging


class Paladin extends Actor with ActorLogging {

  val MaxWorkLoading = 10

  private def getWorkLoading(taskConditions: List[Any])(implicit taskAmount: Int): (Int, List[Any]) = {
    if (taskAmount.toInt <= this.MaxWorkLoading) {
      val soldierAmount = taskAmount.toInt
      (soldierAmount, taskConditions)
    } else {
      val soldierAmount = 10
      (soldierAmount, taskConditions.grouped(10).toList)
    }
  }


  def receive: Receive = {

    case p: PostingSignal =>
      log.info("Here is Paladin !")
//      sender() ! OnWorking("I'm ready !", context.self.path)
      sender() ! "I'm " + context.self.path +  " and I'm ready !"
//      context.self ! OnWorking("I'm ready !", context.self.path)


    case TaskConditions(content, taskAmount, taskCondition) =>
      log.info("[P] content: " + content)
      log.info("[P] taskAmount: " + taskAmount)
      log.info("[P] taskCondition: " + taskCondition)

      log.info("context.self.path.name: " + context.self.path.name)
      log.info("context.self.path.name: " + context.self.path.address)
      log.info("context.parent: " + context.parent)
      log.info("context.parent.path.name: " + context.parent.path.name)

      val (soldierAmount, soldierTasksList) = this.getWorkLoading(taskCondition)(taskAmount.toInt)

      val soldierActorName = AkkaConfig.CrawlerDepartment.crawlSoldierName
      val crawlSoldiers = new Array[ActorRef](soldierAmount)
      for (militaryDogTag <- 0 until taskAmount.toInt) {
        crawlSoldiers(militaryDogTag) = context.actorOf(Props[Soldier], s"$soldierActorName$militaryDogTag")
      }

      crawlSoldiers.foreach(ele => println("[P] All soldier: " + ele.path.name))

      crawlSoldiers.foreach(soldierRef => {
        val soldier = context.actorSelection(soldierRef.path)
        soldier.forward(CrawlWork("Hi, every crawler soldiers, here are your guys tasks.", soldierTasksList))
      })

  }

}


