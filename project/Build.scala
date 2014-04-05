import sbt._

object Build extends Build {
  lazy val root = Project("scrimage", file(".")) aggregate(core, filters, http)
  lazy val core = Project("scrimage-core", file("core"))
  lazy val filters = Project("scrimage-filters", file("filters")).dependsOn(core)
  lazy val http = Project("scrimage-http", file("http")).dependsOn(core)
}