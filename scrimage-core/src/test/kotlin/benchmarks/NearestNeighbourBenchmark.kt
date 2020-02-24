package benchmarks

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.scaling.AwtNearestNeighbourScale
import com.sksamuel.scrimage.scaling.ScrimageNearestNeighbourScale
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@UseExperimental(ExperimentalTime::class)
fun main() {

   val writer = PngWriter.MaxCompression

   val iterations = 100
   val image = ImmutableImage.fromResource("/earth-map-huge.jpg")

   fun bench(w: Int, h: Int) {
      println("Testing scale from ${image.width},${image.height} to $w,$h")
      val duration1 = measureTime {
         repeat(iterations) {
            val scaled = AwtNearestNeighbourScale().scale(image.awt(), w, h)
            require(scaled.width == w)
            require(scaled.height == h)
         }
      }
      println("AWT ($w x $h):")
      println("Total time: " + duration1.inMilliseconds)
      println("Average time: " + duration1.inMilliseconds / iterations)
      println()

      ImmutableImage.fromAwt(AwtNearestNeighbourScale().scale(image.awt(), w, h)).output(writer, "awt_${w}_$h.jpg")

      val duration2 = measureTime {
         repeat(iterations) {
            val scaled = ScrimageNearestNeighbourScale().scale(image.awt(), w, h)
            require(scaled.width == w)
            require(scaled.height == h)
         }
      }

      println("Scrimage ($w x $h):")
      println("Total time: " + duration2.inMilliseconds)
      println("Average time: " + duration2.inMilliseconds / iterations)
      println("-----")

      ImmutableImage.fromAwt( ScrimageNearestNeighbourScale ().scale(image.awt(), w, h)).output(writer, "scrimage_${w}_$h.jpg")
   }

   bench(300, 200)
   bench(1000, 600)
   bench(2000, 1200)
   bench(3200, 1600)
   bench(4800, 2400)


}
