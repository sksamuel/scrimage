
name := "scrimage"

version := "1.3.7"

organization := "com.sksamuel.scrimage"

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

publishArtifact in Test := false

parallelExecution in Test := false

// TODO: This is a user-specific setting and should go in ~/.sbt/credentials.sbt or similar.
credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishTo <<= version {
  (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

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
