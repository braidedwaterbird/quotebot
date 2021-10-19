package quotebot.actors

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import quotebot.model._
import quotebot.model.ActorMessages.NextQuote
import quotebot.utilities.ReadingUtilities
import quotebot.utilities.WritingUtilities
import quotebot.utilities.TwitterUtilities
import quotebot.utilities.TimestampUtilities

object QuoteTweetingActor {


  def apply(readingUtilities: ReadingUtilities, writingUtilities: WritingUtilities, twitterUtilities: TwitterUtilities, timestampUtilities: TimestampUtilities)(implicit conf: QuotebotConf): Behavior[NextQuote] = {
    quoteTweeting(readingUtilities, writingUtilities, twitterUtilities, timestampUtilities)
  }

  private def quoteTweeting(readingUtilities: ReadingUtilities, writingUtilities: WritingUtilities, twitterUtilities: TwitterUtilities, timestampUtilities: TimestampUtilities)(implicit conf: QuotebotConf): Behavior[NextQuote] = 
    Behaviors.receive { (context, message) => 
      val index = readingUtilities.getIndex().getOrElse({
        writingUtilities.writeQuotes()
        1
      })
      val timestamp = timestampUtilities.getTimestamp()
      val overrideTweet = readingUtilities.getOverrideTweet(timestamp)
      val scheduledTweet = readingUtilities.getScheduledTweet(index)
      val tweetToPost = overrideTweet.orElse(scheduledTweet)
      tweetToPost.map(twitterUtilities.postTweet)
      val newIndex = if(index + 1 > conf.quotesToGenerate) {
          writingUtilities.moveLastPostingQuotes()
          writingUtilities.writeQuotes()
          1
        } else (index + 1)
      overrideTweet.foreach(_ => writingUtilities.moveUsedOverride(timestamp))
      writingUtilities.updateIndex(newIndex)
      quoteTweeting(readingUtilities, writingUtilities, twitterUtilities, timestampUtilities)

  }
  
}