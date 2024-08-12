plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   testImplementation(kotlin("stdlib-jdk8"))
   implementation("com.twelvemonkeys.imageio:imageio-core:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.9.4")
   implementation("com.drewnoakes:metadata-extractor:2.18.0")
   implementation("commons-io:commons-io:2.11.0")
   implementation("ar.com.hjg:pngj:2.1.0")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

apply("../publish.gradle.kts")
