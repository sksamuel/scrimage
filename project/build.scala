import com.sun.org.apache.xml.internal.serialize.OutputFormat.Defaults
import sbt._

lazy val standardSettings = Defaults.defaultSettings ++ Seq(
  version := "1.3.4-SNAPSHOT",
  scalaVersion := "2.10.2"
)

object Build extends Build {
  lazy val root = Project("scrimage", file(".")) aggregate(core, filters)
  lazy val core = Product("scrimage-core", file("core"))
  lazy val filters = Product("scrimage-filters", file("filters"))
}