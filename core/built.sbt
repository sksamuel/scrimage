scalaVersion := "2.10.2"

organization := "com.sksamuel.scrimage"

name := "scrimage-core"

version := "1.3.4-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.sanselan" % "sanselan" % "0.97-incubator",
  "log4j" % "log4j" % "1.2.17",
  "org.slf4j" % "slf4j-log4j12" % "1.6.6",
  "org.slf4j" % "slf4j-api" % "1.6.6",
  "commons-io" % "commons-io" % "2.4",
  "org.scalatest" % "scalatest_2.10" % "1.9.1",
  "org.imgscalr" % "imgscalr-lib" % "4.2" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)
