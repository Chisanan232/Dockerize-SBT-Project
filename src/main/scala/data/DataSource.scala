package Dockerize_SBT_Project.src.main.scala.data

import Dockerize_SBT_Project.src.main.scala.config.AkkaConfig

import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.AnalysisException

// Customize a exception
final case class UnexpectedError(private val message: String = "Occur error because of file path. Please check it and program will stop.",
                                 private val cause: Throwable = None.orNull) extends Exception(message, cause)

/***
 * Original data operators
 */
class DataSource {

  val DataFilePath = "src/main/scala/Dockerize_SBT_Project/src/main/resources/all_listed_company.json"
  val DataFilePathInDocker = "Dockerize_SBT_Project/src/main/resources/all_listed_company.json"

  val spark = SparkSession.builder()
    .appName("AKKA with Crawler")
    .master("local[*]")
    .getOrCreate()

  private def readData(): sql.DataFrame = {
    //    spark.read.option("multiline", "true").json(this.DataFilePath)
//    this.spark.read.json(spark.sparkContext.wholeTextFiles(this.DataFilePath).values)
    // How to raise a customized error
    // https://stackoverflow.com/questions/6716719/throw-custom-exception
    try {
      this.spark.read.json(this.DataFilePath)
    } catch {
      case e: AnalysisException =>
        AkkaConfig.InDocker = true
        this.spark.read.json(this.DataFilePathInDocker)
      case _: Throwable => throw new UnexpectedError
    }
  }


  def dataNumber(): Float = {
    val data = this.readData()
    data.count()
  }


  def stockSymbolData(): sql.DataFrame = {
    val data = this.readData()
    data.select("stock_symbol")
  }


  def companyData(): sql.DataFrame = {
    val data = this.readData()
    data.select("company")
  }


  def close_spark(): Unit = {
    /*
    https://stackoverflow.com/questions/50504677/java-lang-interruptedexception-when-creating-sparksession-in-scala
     */
    this.spark.sparkContext.stop()
    this.spark.close()
  }

}

