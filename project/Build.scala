import sbt._

object Build extends Build {
  lazy val root = Project("scrimage", file(".")) aggregate(core, filters, fetch)
  lazy val core = Project("scrimage-core", file("core"))
  lazy val filters = Project("scrimage-filters", file("filters")).dependsOn(core)
  lazy val fetch = Project("scrimage-fetch", file("fetch")).dependsOn(core)
}