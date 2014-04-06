import sbt._
import sbt.Keys._

object Build extends Build {
  val scrimageSettings = Seq(
    organization := "com.sksamuel.scrimage",
    version := "1.3.19",
    scalaVersion := "2.10.3",
    crossScalaVersions := Seq("2.10.3", "2.11.0-RC3")
  )
  lazy val root = Project("scrimage", file(".")).settings(scrimageSettings: _*).aggregate(core, filters, http)
  lazy val core = Project("scrimage-core", file("core")).settings(scrimageSettings: _*)
  lazy val filters = Project("scrimage-filters", file("filters")).dependsOn(core).settings(scrimageSettings: _*)
  lazy val http = Project("scrimage-http", file("http")).dependsOn(core).settings(scrimageSettings: _*)
}