plugins {
   `java-library`
}

dependencies {
   implementation(Libs.TwelveMonkeys.imageIoCore)
   implementation(Libs.TwelveMonkeys.jpeg)
   implementation(Libs.Drewnoaks.metadataExtractor)
   implementation(Libs.Zh.opengif)
   implementation(Libs.Commons.io)
   implementation(Libs.Hjg.pngj)
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

apply("../publish.gradle.kts")
