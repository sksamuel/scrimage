rootProject.name = "scrimage"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

include(
   ":scrimage-app",
   ":scrimage-benchmarks",
   ":scrimage-core",
   ":scrimage-examples",
   ":scrimage-filters",
   ":scrimage-formats-extra",
   ":scrimage-format-png",
   ":scrimage-hash",
   ":scrimage-scala_2.12",
   ":scrimage-scala_2.13",
   ":scrimage-tests",
   ":scrimage-webp",
)

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
   repositories {
      mavenCentral()
      mavenLocal()
      maven("https://oss.sonatype.org/content/repositories/snapshots")
      maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
   }
}
