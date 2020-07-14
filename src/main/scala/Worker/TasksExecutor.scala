package Dockerize_SBT_Project.src.main.scala.Worker

import Dockerize_SBT_Project.src.main.scala.config.AkkaConfig

import scala.sys.process._


class TasksExecutor {

//  val Path = "src/main/scala/Dockerize_SBT_Project/src/main/python"
//  val PathInDOcker = "Dockerize_SBT_Project/src/main/python"
  val Path: String = if (AkkaConfig.InDocker.equals(true)) {
    "Dockerize_SBT_Project/src/main/python"
  } else {
    "src/main/scala/Dockerize_SBT_Project/src/main/python"
  }

  def generateAPIbyPython(date: String, symbol: String): String = {
    val runCmd = s"python3 $Path/multi-lan_stock-crawler_py-ver.py --date $date --listed-company $symbol"
    runCmd.!!
  }


  def runCode(pythonFileArguments: String): String = {
    val runningCmd = s"python3 $Path/multi-lan_stock-crawler_py-ver.py --listed-company $pythonFileArguments"
    println("[INFO] Running Python Code Command Line: \n" + runningCmd)
    println("[DEBUG] running command line result: " + runningCmd.!!)
    runningCmd.!!
  }

}
