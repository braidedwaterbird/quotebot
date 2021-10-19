package quotebot.utilities

import quotebot.model._
import scala.io._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.parser._
import scala.util.Random
import java.nio.file.Files
import java.nio.file.Path
import scala.util.{Failure, Success, Try}

class ReadingUtilities (implicit conf: QuotebotConf){

  val allQuotesFile = s"${conf.resources}/${conf.parentQuotesFile}"

  def getTweet(): Either[io.circe.Error, Quote] = {
    val random = new Random()
    val rawQuotes = Source.fromFile(allQuotesFile).getLines().mkString("")
    val quotes = decode[List[Quote]](rawQuotes)
    quotes.map(quotes => {
      val randomIndex = random.nextInt(quotes.size)
      quotes(randomIndex)
    })
  }

  def getTweets(n: Int): Either[io.circe.Error, Seq[Quote]] = {
    val random = new Random()
    val rawQuotes = Source.fromFile(allQuotesFile).getLines().mkString("")
    val quotes = decode[List[Quote]](rawQuotes)
    quotes.map(quotes => {
      (1 to n).map(number => {
        val randomIndex = random.nextInt(quotes.size)
        quotes(randomIndex)   
      })
    })
  }

  def getOverrideTweet(timestamp: String): Option[Quote] = {
    val overrideFile = s"${conf.resources}/${conf.upcoming}/$timestamp.json"
    scribe.info(s"checking for override file for timestamp $timestamp")
    if(scala.reflect.io.File(overrideFile).exists){
      scribe.info(s"attempting to parse override quote for timestamp $timestamp")
      val rawQuote = Source.fromFile(overrideFile).getLines.mkString
      val quote = decode[Quote](rawQuote)
      quote match {
        case Left(error) => 
          scribe.error(s"error parsing override file for $timestamp: $error")
          None
        case Right(quote) => 
          scribe.info(s"successfully parsed override file for $timestamp")
          scribe.info(s"override $timestamp quote is: $quote")
          Some(quote)
      }
    }
    else None
  }

  def getIndex(): Option[Int] = {
    val quoteFile = s"${conf.resources}/${conf.indexInPostingQuoteFile}"
    Try(Source.fromFile(quoteFile).getLines.mkString.toInt) match {
      case Success(index) => 
        scribe.info(s"found index $index")
        Some(index)
      case Failure(error) =>
        scribe.error(s"index not found: error: $error")
        None
    }
  }

  def getScheduledTweet(index: Int): Option[Quote] = {
    scribe.info(s"getting scheduled tweet with index $index")
    val quoteFile = s"${conf.resources}/${conf.upcoming}/${conf.postingQuotesFile}"
    val rawScheduledQuotes = Source.fromFile(quoteFile).getLines.mkString
    val scheduledQuotes = decode[List[ScheduledQuote]](rawScheduledQuotes)
    scheduledQuotes match {
      case Left(error) =>
        scribe.error(s"error parsing week's quotes file: $error")
        None
      case Right(scheduledQuotes) => {
        scheduledQuotes.find(_.index == index) match {
          case Some(quote) => 
            scribe.info(s"found scheduled quote: $quote")
            Some(quote.quote)
          case None => 
            scribe.error("No scheduled quote found")
            None
        }
        
      }
    }
  }

  
}
