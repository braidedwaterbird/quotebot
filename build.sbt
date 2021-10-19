import Dependencies._

ThisBuild / scalaVersion     := "2.13.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.braidedwaterbird"

val circeVersion = "0.14.1"
val scribeVersion = "3.6.0"
val akkaVersion = "2.6.17"

lazy val root = (project in file("."))
  .settings(
    name := "quotebot",
    libraryDependencies += scalaTest % Test,
    assembly / mainClass := Some("quotebot.actors.Main"),
    assembly / assemblyJarName := "quotebot.jar"
  )

  libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.0"
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
  ).map(_ % circeVersion)
  libraryDependencies += "com.danielasfregola" %% "twitter4s" % "7.0"
  libraryDependencies ++= Seq(
  "com.outr" %% "scribe-slf4j",
  "com.outr" %% "scribe-file"
  ).map(_ % scribeVersion)
  libraryDependencies += "com.enragedginger" %% "akka-quartz-scheduler" % s"1.9.1-akka-2.6.x"
  libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  libraryDependencies += "com.typesafe.akka" %% "akka-protobuf-v3" % akkaVersion
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
