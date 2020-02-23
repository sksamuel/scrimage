plugins {
   `java-library`
}

dependencies {
   api(project(":scrimage-core"))
   implementation(Libs.TwelveMonkeys.bmp)
   implementation(Libs.TwelveMonkeys.tga)
   implementation(Libs.TwelveMonkeys.tiff)
   implementation(Libs.TwelveMonkeys.pcx)
   implementation(Libs.TwelveMonkeys.pnm)
   implementation(Libs.TwelveMonkeys.iff)
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

apply("../publish.gradle.kts")