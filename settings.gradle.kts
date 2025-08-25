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
   }
}
