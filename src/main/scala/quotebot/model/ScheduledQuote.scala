package quotebot.model

case class ScheduledQuote(quote: Quote, index: Int, humanTimestamp: String)
//String is hhmmddyyyy