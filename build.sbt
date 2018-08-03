
organization := "com.phasmid"

name := "Darwin"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.5"
crossScalaVersions := Seq("2.10.6","2.11.8","2.12.5")

val laScalaVersion = "1.0.12-SNAPSHOT"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Resolver.sonatypeRepo("public")
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

val scalaModules = "org.scala-lang.modules"
val scalaModulesVersion = "1.0.4"

val scalaTestGroup = "org.scalatest"
val scalaTestArt = "scalatest"

val scalaCheckGroup = "org.scalacheck"
val scalaCheckArt = "scalacheck"

val typesafeGroup = "com.typesafe"
val configVersion = "1.3.1"
val akkaGroup = "com.typesafe.akka"
lazy val akkaVersion = SettingKey[String]("akkaVersion")
lazy val scalaTestVersion = SettingKey[String]("scalaTestVersion")
lazy val scalaCheckVersion = SettingKey[String]("scalaCheckVersion")

akkaVersion := (scalaBinaryVersion.value match {
  case "2.10" => "2.3.15"
  case "2.11" => "2.4.1"
  case "2.12" => "2.5.4"
})
scalaTestVersion := (scalaBinaryVersion.value match {
  case "2.10" => "2.2.6"
  case "2.11" => "3.0.1"
  case "2.12" => "3.0.5"
})
scalaCheckVersion := (scalaBinaryVersion.value match {
  case "2.10" => "1.12.6"
  case "2.11" => "1.12.6"
  case "2.12" => "1.13.5"
})

libraryDependencies ++= (scalaBinaryVersion.value match {
  case "2.12" =>   Seq(
    scalaModules %% "scala-parser-combinators" % scalaModulesVersion,
    // NOTE: we don't need this but dependencies apparently use different versions:
    scalaModules %% "scala-xml" % "1.0.6",
    akkaGroup %% "akka-actor" % akkaVersion.value,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.0"
  )
  case "2.11" =>   Seq(
    scalaModules %% "scala-parser-combinators" % scalaModulesVersion,
    // NOTE: we don't need this but dependencies apparently use different versions:
    scalaModules %% "scala-xml" % scalaModulesVersion,
    // NOTE: only used for testing
    akkaGroup %% "akka-actor" % akkaVersion.value,
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  )
  case "2.10" =>   Seq(
    akkaGroup %% "akka-actor" % akkaVersion.value,
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  )
  case _ => Seq()
})

libraryDependencies ++= Seq(
  "com.phasmid" %% "lascala" % laScalaVersion,
  "joda-time" % "joda-time" % "2.9.9",
  "org.clapper" %% "classutil" % "1.1.2",
  typesafeGroup % "config" % configVersion,
  "ch.qos.logback" %  "logback-classic" % "1.1.7" % "runtime",
  scalaCheckGroup %% scalaCheckArt % scalaCheckVersion.value % "test",
  scalaTestGroup %% scalaTestArt % scalaTestVersion.value % "test"
)

//libraryDependencies ++= Seq(
//  scalaModules %% "scala-parser-combinators" % scalaModulesVersion,
//  scalaModules %% "scala-xml" % "1.0.6",
//	akkaGroup %% "akka-actor" % akkaVersion.value,
//	"org.scalatest" %% "scalatest" % scalaTestVersion.value % "test"
//)
