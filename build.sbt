name := "Scout-Core"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)