package quotebot.actors

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.NotUsed
import com.typesafe.akka.extension.quartz.QuartzSchedulerTypedExtension
import quotebot.actors.QuoteTweetingActor
import quotebot.model.ActorMessages
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import quotebot.utilities._
import quotebot.model._
import scribe._
import scribe.file._


object Main {

  def apply(): Behavior[NotUsed] = 
    Behaviors.setup { context => 
      val maybeConfig = ConfigSource.default.load[ServiceConf]
      maybeConfig match {
        case Right(conf) => scribe.info(s"config on load: $conf")
        case Left(error) => scribe.error(s"error loading config: $error")
      }
      implicit val conf = maybeConfig.toOption.get.quotebotConf
      val logPath = conf.logPath
      scribe.Logger.root.withHandler(writer = FileWriter(logPath / (year % "-" % month % ".log"))).replace()

      val twitterUtilities = new TwitterUtilities()
      val readingUtilities = new ReadingUtilities()
      val timestampUtilities = new TimestampUtilities()
      val writingUtilities = new WritingUtilities(readingUtilities, timestampUtilities)
      val scheduleName = "quotebotschedule"
      val quoteTweetingActorName = "quotebot"

      val quoteTweetingActor = context.spawn(QuoteTweetingActor(readingUtilities, writingUtilities, twitterUtilities, timestampUtilities), quoteTweetingActorName)
      
      val scheduler = QuartzSchedulerTypedExtension(context.system)
      scheduler.scheduleTyped(scheduleName, quoteTweetingActor, ActorMessages.NextQuote())

      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }
    
  def main(args: Array[String]): Unit = {
    ActorSystem(Main(), "TweetBot")
  }
  

}

