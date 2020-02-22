plugins {
   `java-library`
}

dependencies {
   implementation(Libs.commons.io)
   implementation(project(":scrimage-core"))
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}