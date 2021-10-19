package quotebot.utilities

import quotebot.model._
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.TwitterRestClient

class TwitterUtilities (implicit conf: QuotebotConf){

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val restClient = TwitterRestClient()

  def postTweet(quote: Quote) = {

    val response = restClient.createTweet(quote.toTweet)
    response.onComplete(x => println(s"response is: $response"))
  }


}