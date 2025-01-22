plugins {
   `java-library`
   kotlin("jvm")
   id("me.champeau.jmh") version "0.7.2"
}

dependencies {
   jmh(project(":scrimage-core"))
   jmh(project(":scrimage-format-png"))
   jmh("org.imgscalr:imgscalr-lib:4.2")
   jmh("net.coobird:thumbnailator:0.4.18")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

jmh {
   warmupIterations.set(1)
   iterations.set(3)
   fork.set(1)
   batchSize.set(5)
   operationsPerInvocation.set(5)
   warmup.set("1s")
   warmupBatchSize.set(5)
   zip64.set(true)
   duplicateClassesStrategy.set(DuplicatesStrategy.EXCLUDE)
   profilers.set(listOf("gc"))
   if (project.hasProperty("includes")) {
      includes.set(listOf(project.property("includes") as String))
   }
}
