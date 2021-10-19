package example

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.time.ZonedDateTime
import java.time.ZoneId
import quotebot.utilities.TimestampUtilities
import quotebot.utilities.WritingUtilities
import quotebot.utilities.ReadingUtilities
import quotebot.model.QuotebotConf
import pureconfig.ConfigSource
import pureconfig.generic.auto._
class TimestampUtilitiesSpec extends AnyFlatSpec with Matchers  {

  //of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zone)  

  implicit val conf = QuotebotConf()

  val timestampUtilities = new TimestampUtilities()
  val quotesToGenerate = conf.quotesToGenerate

  val timezone = ZoneId.of(conf.timezone)

  "The override quote timestamp" should "return correctly" in {
    val baseCase = ZonedDateTime.of(2018, 11, 15, 8, 1, 0, 0, timezone)

    val changeHour = ZonedDateTime.of(2018, 11, 15, 8, 41, 0, 0, timezone)

    val changeDate = ZonedDateTime.of(2018, 11, 15, 23, 41, 0, 0, timezone)
    
    val changeMonth = ZonedDateTime.of(2018, 11, 30, 23, 41, 0, 0, timezone)

    val changeFebruary = ZonedDateTime.of(2018, 2, 28, 23, 41, 0, 0, timezone)
    val notChangeFebruyInLeapYear = ZonedDateTime.of(2020, 2, 28, 23, 41, 0, 0, timezone)

    val changeYear = ZonedDateTime.of(2018, 12, 31, 23, 31, 0, 0, timezone)

    timestampUtilities.buildAndCheckTimestamp(baseCase) shouldBe "08-15-11-2018"
    timestampUtilities.buildAndCheckTimestamp(changeHour) shouldBe "09-15-11-2018"
    timestampUtilities.buildAndCheckTimestamp(changeDate) shouldBe "24-16-11-2018"
    timestampUtilities.buildAndCheckTimestamp(changeMonth) shouldBe "24-01-12-2018"
    timestampUtilities.buildAndCheckTimestamp(changeFebruary) shouldBe "24-01-03-2018"
    timestampUtilities.buildAndCheckTimestamp(notChangeFebruyInLeapYear) shouldBe "24-29-02-2020"
    timestampUtilities.buildAndCheckTimestamp(changeYear) shouldBe "24-01-01-2019"
  }

  "The human timestamp" should "return correctly" in {
    timestampUtilities.getHumanTimestamp(1) shouldBe "day 1 quote 1"
    timestampUtilities.getHumanTimestamp(13) shouldBe "day 1 quote 13"
    timestampUtilities.getHumanTimestamp(25) shouldBe "day 2 quote 1"
    timestampUtilities.getHumanTimestamp(51) shouldBe "day 3 quote 3"
    timestampUtilities.getHumanTimestamp(24) shouldBe "day 1 quote 24"
    timestampUtilities.getHumanTimestamp(quotesToGenerate) shouldBe "day 7 quote 24"
  }
}
