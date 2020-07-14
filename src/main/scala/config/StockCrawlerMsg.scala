package Dockerize_SBT_Project.src.main.scala.config

/***
 * All message and data conditions definitions
 */
trait StockCrawlerMsg {
  val content: String
}

case class StockDataRequirement(content: String, taskNum: Float, taskConditions: List[Any]) extends StockCrawlerMsg
case class PostingSignal(content: String) extends StockCrawlerMsg
case class TaskConditions(content: String, taskAmount: Float, taskCondition: List[Any]) extends StockCrawlerMsg
case class CrawlWork(content: String, stockSymbol: List[Any]) extends StockCrawlerMsg
case class GotData(content: String, data: String) extends StockCrawlerMsg
case class NeedCrawlerData(content: String) extends StockCrawlerMsg
case class TargetAPI(content: String, api: String) extends StockCrawlerMsg
case class FinishProgram(content: String, part: String) extends StockCrawlerMsg
