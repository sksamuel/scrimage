import com.typesafe.sbt.SbtPgp
import sbt.Keys._
import sbt._

/** Adds common settings automatically to all subprojects */
object Build extends AutoPlugin {

  object autoImport {
    val org = "com.sksamuel.scrimage"
    val TwelveMonkeysVersion = "3.4.2"
    val PngjVersion = "2.1.0"
    val MetadataExtractorVersion = "2.12.0"
    val ScalatestVersion = "3.0.8"
    val CommonsIoVersion = "2.6"
  }

  import autoImport._

  def isTravis = System.getenv("TRAVIS") == "true"
  def travisBuildNumber = System.getenv("TRAVIS_BUILD_NUMBER")

  override def trigger = allRequirements
  override def projectSettings = publishingSettings ++ Seq(
    organization := org,
    name := "scrimage",
    scalaVersion := "2.13.1",
    crossScalaVersions := Seq("2.12.10", "2.13.1"),
    parallelExecution in Test := false,
    scalacOptions := Seq(
      "-unchecked",
      "-encoding", "utf8",
      "-deprecation",
      "-explaintypes",
      "-feature",
      // optimizations
      "-opt:l:method",
      "-Xcheckinit"
      ),
    javacOptions := Seq("-source", "1.11", "-target", "1.11"),
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.29",
      "org.imgscalr" % "imgscalr-lib" % "4.2" % "test",
      "org.scalatest" %% "scalatest" % ScalatestVersion % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test"
      )
    )

  val publishingSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    SbtPgp.autoImport.useGpg := true,
    SbtPgp.autoImport.useGpgAgent := true,
    if (isTravis) {
      credentials += Credentials(
        "Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        sys.env.getOrElse("OSSRH_USERNAME", ""),
        sys.env.getOrElse("OSSRH_PASSWORD", "")
        )
    } else {
      credentials += Credentials(Path.userHome / ".sbt" / "credentials.sbt")
    },
    if (isTravis) {
      version := s"3.0.0.$travisBuildNumber-SNAPSHOT"
    } else {
      version := "3.0.0-RC5"
    },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isTravis) {
        Some("snapshots" at s"${nexus}content/repositories/snapshots")
      } else {
        Some("releases" at s"${nexus}service/local/staging/deploy/maven2")
      }
    },
    pomExtra := {
      <url>https://github.com/sksamuel/scrimage</url>
        <licenses>
          <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:git@github.com:sksamuel/scrimage.git</connection>
          <developerConnection>scm:git:git@github.com:sksamuel/scrimage.git</developerConnection>
          <url>git@github.com:sksamuel/scrimage.git</url>
        </scm>
        <developers>
          <developer>
            <name>Stephen Samuel</name>
            <email>sam@sksamuel.com</email>
            <timezone>GMT</timezone>
          </developer>
        </developers>
    }
    )
}