plugins {
   `java-library`
}

dependencies {
   api(Libs.TwelveMonkeys.imageIoCore)
   api(Libs.TwelveMonkeys.imageIoJpeg)
   api(Libs.Drewnoaks.metadataExtractor)
   implementation(Libs.commons.io)
   implementation(Libs.Hjg.pngj)
//   "com.twelvemonkeys.common"  % "common-lang"         % TwelveMonkeysVersion,
//   "com.twelvemonkeys.common"  % "common-io"           % TwelveMonkeysVersion,
//   "com.twelvemonkeys.common"  % "common-image"        % TwelveMonkeysVersion,
//   "commons-io"                % "commons-io"          % CommonsIoVersion,
//   "org.apache.commons"        % "commons-lang3"       % "3.9"
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}