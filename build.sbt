lazy val root = (project in file("."))
  .settings(
    publish := {},
    publishArtifact := false,
    name := "scrimage"
  )
  .aggregate(scrimageCore, scrimageScala, scrimageIoExtra, scrimageFilters)

lazy val scrimageCore = (project in file("scrimage-core"))
  .settings(
    name := "scrimage-core"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twelvemonkeys.imageio" % "imageio-core"        % TwelveMonkeysVersion,
      "com.twelvemonkeys.imageio" % "imageio-jpeg"        % TwelveMonkeysVersion,
      "com.twelvemonkeys.common"  % "common-lang"         % TwelveMonkeysVersion,
      "com.twelvemonkeys.common"  % "common-io"           % TwelveMonkeysVersion,
      "com.twelvemonkeys.common"  % "common-image"        % TwelveMonkeysVersion,
      "com.drewnoakes"            % "metadata-extractor"  % MetadataExtractorVersion,
      "commons-io"                % "commons-io"          % CommonsIoVersion,
      "ar.com.hjg"                % "pngj"                % PngjVersion,
      "org.apache.commons"        % "commons-lang3"       % "3.9"
    )
  )

lazy val scrimageScala = (project in file("scrimage-scala"))
  .settings(name := "scrimage-scala")
  .dependsOn(scrimageCore)

lazy val scrimageIoExtra = (project in file("scrimage-io-extra"))
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
  ).dependsOn(scrimageCore)

lazy val scrimageFilters = (project in file("scrimage-filters"))
  .dependsOn(scrimageCore)
  .settings(
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.6"
    ),
    name := "scrimage-filters"
  )
