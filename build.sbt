
organization := "com.phasmid"

name := "Darwin"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.10.6","2.11.8","2.12.3")


val scalaModules = "org.scala-lang.modules"
val scalaModulesVersion = "1.0.6"

val typesafeGroup = "com.typesafe"
val configVersion = "1.3.1"
// NOTE: Akka is used only for testing this package.
val akkaGroup = "com.typesafe.akka"
lazy val akkaVersion = SettingKey[String]("akkaVersion")
lazy val scalaTestVersion = SettingKey[String]("scalaTestVersion")

akkaVersion := (scalaBinaryVersion.value match {
	case "2.10" => "2.3.15"
	case "2.11" => "2.4.1"
	case "2.12" => "2.5.4"
})
scalaTestVersion := (scalaBinaryVersion.value match {
	case "2.10" => "2.2.6"
	case "2.11" => "3.0.1"
	case "2.12" => "3.0.1"
})


ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Resolver.sonatypeRepo("public")
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= (scalaBinaryVersion.value match {
  case "2.12" =>   Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.3",
    // NOTE: we don't need this but dependencies apparently use different versions:
    "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
    "ch.qos.logback" %  "logback-classic" % "1.2.3" % "runtime",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  )
  case "2.11" =>   Seq(
    "org.scala-lang" % "scala-reflect" % "2.11.3",
    scalaModules %% "scala-parser-combinators" % scalaModulesVersion,
    // NOTE: we don't need this but dependencies apparently use different versions:
    scalaModules %% "scala-xml" % scalaModulesVersion,
    "org.scalacheck" %% "scalacheck" % "1.13.2" % "test",
    "ch.qos.logback" %  "logback-classic" % "1.2.3" % "runtime",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  )
  case _ => Seq()
})


libraryDependencies ++= Seq(
  scalaModules %% "scala-parser-combinators" % scalaModulesVersion,
  scalaModules %% "scala-xml" % "1.0.6",
	"joda-time" % "joda-time" % "2.9.9",
  "com.phasmid" %% "lascala" % "1.0.8-SNAPSHOT",
  "org.clapper" %% "classutil" % "1.1.2",
	akkaGroup %% "akka-actor" % akkaVersion.value,
	"org.scalatest" %% "scalatest" % scalaTestVersion.value % "test"
)
