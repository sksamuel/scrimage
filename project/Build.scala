import sbt._
import sbt.Keys._

object Build extends Build {
  val scrimageSettings = Seq(
    organization := "com.sksamuel.scrimage",
    version := "1.3.20",
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.10.4", "2.11.0")
  )
  lazy val root = Project("scrimage", file(".")).settings(scrimageSettings: _*).aggregate(core, filters)
  lazy val core = Project("scrimage-core", file("core")).settings(scrimageSettings: _*)
  lazy val filters = Project("scrimage-filters", file("filters")).dependsOn(core).settings(scrimageSettings: _*)
  lazy val samples = Project("scrimage-samples", file("samples")).dependsOn(core).settings(scrimageSettings: _*)
  //lazy val http = Project("scrimage-http", file("http")).dependsOn(core).settings(scrimageSettings: _*)
}