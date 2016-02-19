import com.typesafe.sbt.SbtPgp._
import sbt.Keys._
import sbt._

object Build extends Build {

  val TwelveMonkeysVersion = "3.2.1"
  val PngjVersion = "2.1.0"
  val MetadataExtractorVersion = "2.8.1"
  val ScalatestVersion = "3.0.0-M12"

  val scrimageSettings = Seq(
    organization := "com.sksamuel.scrimage",
    name := "scrimage",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.6", "2.11.7", "2.12.0-M3"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    scalacOptions := Seq("-unchecked", "-encoding", "utf8"),
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "org.slf4j"             %     "slf4j-api"         % "1.7.7",
      "org.imgscalr"          %     "imgscalr-lib"      % "4.2"                % "test",
      "org.scalatest"         %%    "scalatest"         % ScalatestVersion     % "test",
      "org.mockito"           %     "mockito-all"       % "1.9.5"              % "test"
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    useGpg := true,
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    pomIncludeRepository := { _ => false },
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

  lazy val root = Project("scrimage", file("."))
    .settings(scrimageSettings: _*)
    .settings(publishArtifact := false)
    .aggregate(core, filters, io)

  lazy val core = Project("scrimage-core", file("scrimage-core"))
    .settings(scrimageSettings: _*)
    .settings(name := "scrimage-core")
    .settings(
      libraryDependencies ++= Seq(
        "com.twelvemonkeys.imageio" % "imageio-core"        % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-jpeg"        % TwelveMonkeysVersion,
        "com.twelvemonkeys.common"  % "common-lang"         % TwelveMonkeysVersion,
        "com.twelvemonkeys.common"  % "common-io"           % TwelveMonkeysVersion,
        "com.twelvemonkeys.common"  % "common-image"        % TwelveMonkeysVersion,
        "com.drewnoakes"            % "metadata-extractor"  % MetadataExtractorVersion,
        "commons-io"                % "commons-io"          % "2.4",
        "ar.com.hjg"                % "pngj"                % PngjVersion,
        "org.apache.commons"        % "commons-lang3"       % "3.3.2" % "test"
      )
    )

  lazy val io = Project("scrimage-io-extra", file("scrimage-io-extra"))
    .settings(scrimageSettings: _*)
    .settings(name := "scrimage-io-extra")
    .settings(
      libraryDependencies ++= Seq(
        "com.twelvemonkeys.imageio" % "imageio-bmp"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-jpeg"      % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-icns"      % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-iff"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-pcx"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-pict"      % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-pdf"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-pnm"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-psd"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-sgi"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-tiff"      % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-tga"       % TwelveMonkeysVersion,
        "com.twelvemonkeys.imageio" % "imageio-thumbsdb"  % TwelveMonkeysVersion
      )
    ).dependsOn(core)

  lazy val filters = Project("scrimage-filters", file("scrimage-filters"))
    .dependsOn(core)
    .settings(scrimageSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "commons-io" % "commons-io" % "2.4"
      ),
      name := "scrimage-filters"
    )
}
