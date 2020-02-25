package com.sksamuel.scrimage.benchmarks

import com.sksamuel.scrimage.ImmutableImage
import net.coobird.thumbnailator.Thumbnails
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val images = listOf(
   "/bears.jpg",
   "/elephant.jpg",
   "/falcon.jpg"
//   "/fox.jpg",
//   "/heron.jpg",
//   "/leopard.jpg",
//   "/lion.jpg",
//   "/peacock.jpg",
//   "/rhinoj.jpg"
)

private val libraries = listOf(
   Thumbnailator, Scrimage
)

@UseExperimental(ExperimentalTime::class)
fun main() {
   for (library in libraries) {
      println("Starting library ${library.name}")
      val duration = measureTime {
         for (image in images) {
            val scaled = library.scale(image, 600, 400)
            ImageIO.write(scaled, "JPEG", File(library.name + "_" + image.removePrefix("/")))
         }
      }
      println("Duration: ${duration.inMilliseconds}ms")
   }
}

interface Scale {
   fun scale(resource: String, width: Int, height: Int): BufferedImage
   val name: String
      get() = javaClass.simpleName
}

object Thumbnailator : Scale {
   override fun scale(resource: String, width: Int, height: Int): BufferedImage {
      return Thumbnails.of(javaClass.getResourceAsStream(resource))
         .size(width, height)
         .keepAspectRatio(false)
         .asBufferedImage()
   }
}

object Scrimage : Scale {
   override fun scale(resource: String, width: Int, height: Int): BufferedImage {
      return ImmutableImage.loader()
         .detectMetadata(false)
         .detectOrientation(false)
         .fromResource(resource)
         .scaleTo(width, height).awt()
   }
}
