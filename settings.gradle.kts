rootProject.name = "scrimage"

include(
   ":scrimage-app",
   ":scrimage-benchmarks",
   ":scrimage-core",
   ":scrimage-examples",
   ":scrimage-filters",
   ":scrimage-formats-extra",
   ":scrimage-format-png",
   ":scrimage-scala_2.12",
   ":scrimage-scala_2.13",
   ":scrimage-tests",
   ":scrimage-webp",
)

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
