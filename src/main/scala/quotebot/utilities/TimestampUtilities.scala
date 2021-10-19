package quotebot.utilities

import quotebot.model._
import java.util.Calendar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters._
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

class TimestampUtilities (implicit conf: QuotebotConf) {

  val formatter = DateTimeFormatter.ofPattern(conf.overrideQuoteTimestampFormat)

  def getTimestamp(): String = {
    val now = LocalDateTime.now()
    val reallyNow = now.atZone(ZoneId.of(conf.timezone))
    buildAndCheckTimestamp(reallyNow)
  }

  def buildAndCheckTimestamp(datetime: ZonedDateTime): String = {
    val minute = datetime.getMinute()
    val secondHalfOfHour = minute > 30
    val realDatetime = if(secondHalfOfHour) datetime.plusHours(1) else datetime//+ 1 else reallyNow.getHour()
    realDatetime.format(formatter)
  }

    def getHumanTimestamp(index: Int): String = {
    val mod24 = index % 24
    val div24 = index / 24
    val day = if(mod24 == 0) div24 else div24 + 1
    val quoteNum = if(mod24 == 0) 24 else mod24

    s"day $day quote $quoteNum"
  }
}