package Dockerize_SBT_Project.src.main.scala.Worker

import Dockerize_SBT_Project.src.main.scala.config._

import java.io.File

import scala.collection.mutable.ListBuffer

import scala.util.matching.Regex
import scala.sys.process._
import akka.actor.{Actor, ActorLogging}



/***
 * The command operations
 */
protected class RunningCommand2 {

//  val Path = "/Users/bryantliu/IdeaProjects/KobeDataScience/src/main/python/Taiwan_Stock_Market/"
  val Path = "/Users/bryantliu/IdeaProjects/KobeDataScience/src/main/scala/Taiwan_stock_market_crawler/crawler_running_code/"

  def chkCodeFile(implicit targetPythonFile: String): Boolean = {
    val pythonFilePattern: Regex = ".{1,10000}.py".r
    val mappingResult = pythonFilePattern.findFirstIn(targetPythonFile)
    if (mappingResult != None) {
      true
    } else {
      false
    }
  }


  def chkCodeFiles(implicit targetPythonFile: String): Boolean = {
    val file = new File(this.Path)

    if (file.isDirectory) {
      val files = file.listFiles().toList

      val pythonFilePattern: Regex = ".{1,10000}.py".r
      var notPythonFlag = 0
      println(files)
      for (index <- 1 to files.length) {
        println(files.take(index))
        val mappingResult = pythonFilePattern.findFirstIn(files.take(index).toString)
        if (mappingResult != None) {
          println("[OK] This is a Python type file.")
        } else {
          notPythonFlag += 1
          println("[WARNING] This is not a Python type file.")
        }
      }

      if (notPythonFlag != 0) {
        false
      } else {
        true
      }
    } else {
      false
    }
  }


  def runCode(pythonFileArguments: String)(implicit targetPythonFile: String): String = {
    val runningCmd = s"python $targetPythonFile  --listed-company $pythonFileArguments"
    println("[INFO] Running Python Code Command Line: \n" + runningCmd)
    println("[DEBUG] running command line: " + runningCmd.!!)
    runningCmd.!!
  }

}


class Soldier extends Actor with ActorLogging{

  val cmdExecutor = new TasksExecutor

  def receive: Receive = {

    case TargetAPI(content, api) =>
      log.info("Get the API info !")
      log.info(s"API info is $api")

      val targetData = this.cmdExecutor.runCode(api)
      val dataSaver = context.actorSelection(AkkaConfig.DataAnalyserDepartment.ProducerWorkerPath)
      dataSaver ! GotData("", targetData)


    case CrawlWork(content, taskList) =>
      println("This is worker, Start work !")
      println("[S] content: " + content)
      println("[S] taskList: " + taskList)

      // Method 1
      var cmdReturnList = new ListBuffer[String]()
      taskList.foreach(api => {
        val cmdReturnValue: String = this.cmdExecutor.runCode(api.toString)
        cmdReturnList += cmdReturnValue
        println("************** Command Running Result **************")
        println("runningResult: ")
        println(cmdReturnValue)
        println("****************************************************")
        sender() ! GotData("I'm Soldier and I finish the task.", cmdReturnValue)
      })

//      // Method 2
//      val taskResults = taskList.map(api => this.running_cmd.runCode(api.toString))
//      println("************** Command Running Result **************")
//      println("runningResult: ")
//      println(taskResults)
//      println("****************************************************")
//
//      println("[Soldier] context.path: " + context.self.path)
//      println("[Soldier] context.path.name: " + context.self.path.name)
//      sender() ! GotData("I'm Soldier and I finish the task.", taskResults)

  }

}

