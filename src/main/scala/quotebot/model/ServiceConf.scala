package quotebot.model

import pureconfig._

case class ServiceConf(
  quotebotConf: QuotebotConf
)

case class QuotebotConf(
  resources: String = "src/main/resources",
  parentQuotesFile: String = "parent_quotes.json",
  logPath: String = "logs",
  upcoming: String = "upcoming",
  past: String = "past",
  postingQuotesFile: String = "posting_quotes.json",
  indexInPostingQuoteFile: String = "index.txt",
  quotesToGenerate: Int = 168,
  twitterCharMax: Int = 280,
  timezone: String = "America/New_York",
  overrideQuoteTimestampFormat: String = "kk-dd-MM-yyyy",
  saveOldPostingFiles: Boolean = true
)