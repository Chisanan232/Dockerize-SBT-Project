package Dockerize_SBT_Project.src.main.scala.data

import org.joda.time.{DateTime, Period}

import scala.collection.mutable.ListBuffer


class APIDate {

  val fromDateYear = 2020
  val fromDateMonth = 6
  val fromDateDay = 1
  var toDateYear = 0
  var toDateMonth = 0
  var toDateDay = 0

  private def nowDatetime(): DateTime = {
    DateTime.now()
  }


  private def datetimeRange(from: DateTime, period: Period)(implicit to: DateTime): Iterator[DateTime] = {
    Iterator.iterate(from)(_.plus(period)).takeWhile(!_.isAfter(to))
  }


  private def datetimeList(datetime: List[DateTime]): List[String] = {
    var dateList = new ListBuffer[String]()
    datetime.foreach(dateElement => {
      dateList += List(dateElement.getYear.toString, dateElement.getYear.toString, dateElement.getYear.toString).mkString("/")
    })
    dateList.toList
  }


  def targetDateRange(): List[String] = {

    def getToDate(): DateTime = {
      if (this.toDateYear != 0 && this.toDateYear != 0 && this.toDateYear != 0) {
        new DateTime().withYear(this.toDateYear).withMonthOfYear(this.toDateMonth).withDayOfMonth(this.toDateDay)
      }
      else {
        this.nowDatetime()
      }
    }

    val fromDate = new DateTime().withYear(this.fromDateYear).withMonthOfYear(this.fromDateMonth).withDayOfMonth(this.fromDateDay)
    implicit val toDate = getToDate()
    val allDate = this.datetimeRange(fromDate, new Period().withDays(1))
    this.datetimeList(allDate.toList)
  }

}


//object TestDateDataGeneration extends App {
//  val apiData = new APIDate
//  val result = apiData.targetDateRange()
//  println(result)
//}
