repositories {
   mavenCentral()
}

plugins {
   `kotlin-dsl`
}

dependencies {
   implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
   implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.35.0")
}
