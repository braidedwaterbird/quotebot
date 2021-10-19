package quotebot.model

import io.circe.generic.auto._
import io.circe.syntax._

case class Quote(chapterName: String, lines: Seq[String] = Seq.empty[String])(implicit conf: QuotebotConf){
  def toTweet: String = {
    (lines :+ s"-c$chapterName").mkString("\n\n")
  }

  def fitsInCharLimit: Boolean = {
    toTweet.size <= conf.twitterCharMax
  }

  def addLine(line: String): Quote = {
    this.copy(lines = lines :+ line)
  }

}