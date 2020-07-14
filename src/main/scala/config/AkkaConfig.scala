package Dockerize_SBT_Project.src.main.scala.config

object AkkaConfig {

  var InDocker = false

  val SystemName = "CauchySystem"
  val AllKingName = "King"
  val AllKingPath = ""

  val CrawlerNumber = 2
  val ProducerNumber = 2
  val ConsumerNumber = 2

  object CrawlerDepartment {
    /****
     * Parameters about crawler part.
     */

    val crawlKingName = "crawler_King"
    var KingPath = ""
    val crawlerPaladinName = "crawler_Paladin"
    var crawlerPaladinPath = ""
    val crawlSoldierName = "soldier_"
    var crawlSoldierPath = ""
    val consumerGroupID = "crawler"
    val dataSaverName = ""
    val dataSaverPath = ""
  }


  object DataAnalyserDepartment {
    /****
     * Parameters about data analyser part.
     */

    val DataKingName = "DataImportKafkaKing"
    var KingPath = ""
    val DataPremierName = "DataImportKafkaPremier"
    var PremierPath = ""
    val DistributeWorkerName = "DataDistributeWorker"
    val DistributeWorkerPath = ""
    val ProducerLeaderName = "KafkaProducerLeader"
    var ProducerLeaderPath = ""
    val ProducerWorkerName = "producer_"
    var ProducerWorkerPath = ""
    val ConsumerLeaderName = "KafkaConsumerLeader"
    var ConsumerLeaderPath = ""
    val ConsumerWorkerName = "consumer_"
    var ConsumerWorkerPath = ""
  }

}
