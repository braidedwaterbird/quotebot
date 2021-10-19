package quotebot.utilities

import quotebot.model.QuotebotConf
import quotebot.model.ScheduledQuote
import java.io.BufferedWriter
import java.io.FileWriter
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.nio.file.StandardCopyOption
import java.nio.file.Files

class WritingUtilities(readingUtilities: ReadingUtilities, timestampUtilities: TimestampUtilities) (implicit conf: QuotebotConf) {

  def moveLastPostingQuotes(): Unit = {
    val oldPostingQuotes = new File(s"${conf.resources}/${conf.upcoming}/${conf.postingQuotesFile}").toPath
    val now = LocalDateTime.now()
    val reallyNow = now.atZone(ZoneId.of(conf.timezone))
    val date = reallyNow.getDayOfMonth()
    val month = reallyNow.getMonthValue()
    val year = reallyNow.getYear()
    val newFileName = s"${date}_${month}_${year}"
    val newLocation = new File(s"${conf.resources}/${conf.past}/$newFileName.json").toPath
    scribe.info(s"moving old posting quotes file to $newLocation")
    Files.move(oldPostingQuotes, newLocation, StandardCopyOption.ATOMIC_MOVE)
    scribe.info(s"old posting quotes file moved to $newLocation")
  }

  def moveUsedOverride(timestamp: String): Unit = {
    val overrideFile = new File(s"${conf.resources}/${conf.upcoming}/$timestamp.json").toPath
    val newLocation = new File(s"${conf.resources}/${conf.past}/$timestamp.json").toPath
    scribe.info(s"moving override file with timestamp $timestamp to $newLocation")
    Files.move(overrideFile, newLocation, StandardCopyOption.ATOMIC_MOVE)
    scribe.info(s"override file with timestame $timestamp moved to $newLocation")
  }

  def writeQuotes(): Unit = {
    val quotes = readingUtilities.getTweets(conf.quotesToGenerate)
    quotes match {
      case Left(error) => scribe.error(s"error generating quotes to write: $error")
      case Right(quotes) =>
        scribe.info("writing quotes")
        val scheduledQuotes = (1 to quotes.size).map(i => {
          val humanTimestamp = timestampUtilities.getHumanTimestamp(i)    
          ScheduledQuote(quotes((i - 1)), i, humanTimestamp)

        })
        val file = s"${conf.resources}/${conf.upcoming}/${conf.postingQuotesFile}"
        val bw = new BufferedWriter(new FileWriter(file))
        val json = scheduledQuotes.asJson
        bw.write(json.toString)
        bw.close()
        scribe.info("quotes written")
    }
  }

  def updateIndex(newIndex: Int): Unit = {
    val file = s"${conf.resources}/${conf.indexInPostingQuoteFile}"
    val bw = new BufferedWriter(new FileWriter(file))
    scribe.info("updating index")
    bw.write(newIndex.toString)
    bw.close()
    scribe.info("index updated")
  }
  
}
