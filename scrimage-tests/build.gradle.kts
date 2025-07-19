plugins {
   id("java-conventions")
   id("testing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   implementation(project(":scrimage-filters"))
   implementation("com.twelvemonkeys.imageio:imageio-core:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.12.0")
   implementation("com.drewnoakes:metadata-extractor:2.19.0")
   implementation("commons-io:commons-io:2.11.0")
}
