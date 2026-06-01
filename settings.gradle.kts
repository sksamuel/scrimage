rootProject.name = "scrimage"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

include(
   ":scrimage-benchmarks",
   ":scrimage-core",
   ":scrimage-examples",
   ":scrimage-filters",
   ":scrimage-formats-extra",
   ":scrimage-hash",
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
