@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.wordSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.io.IOUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.Duration

class StreamingGifWriterTest : WordSpec({
   // run dispose method tests
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/animated_birds.gif")) // tests 'doNotDispose'
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/bananaDance.gif")) // tests 'restoreToBackgroundColor'
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/canvas_prev.gif")) // tests 'restoreToPrevious'

   val bird = ImmutableImage.loader().fromResource("/bird_small.png")

   "StreamingGifWriter" should {
      "write to memory" {
         val writer = StreamingGifWriter().withFrameDelay(Duration.ofMillis(500))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(bird)
         stream.writeFrame(bird.flipX())
         stream.writeFrame(bird.flipY())
         stream.writeFrame(bird.flipX())
         stream.writeFrame(bird.flipY())
         stream.close()
         val bytes = output.toByteArray()
         bytes shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/streaming_gif_writer_output_test.gif"))
      }

      "use transparent pixels when compression is set" {
         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val input = AnimatedGifReader.read(ImageSource.of(javaClass.getResourceAsStream("/gif/fallingroof.gif")))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         input.frames.forEach { frame ->
            val scaled = frame.scaleTo(400, 280).copy(BufferedImage.TYPE_INT_ARGB)
            stream.writeFrame(scaled)
         }
         stream.close()
         val bytes = output.toByteArray()
         bytes shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/gif/fallingroof_scaled.gif"))
      }

      "not mutate caller's ImmutableImage in compressed mode" {
         // Two 8x8 images where most pixels match, so the compressed diff path
         // will attempt to zero-fill matching cells.
         val image1 = ImmutableImage.create(8, 8, BufferedImage.TYPE_INT_ARGB)
         val image2 = ImmutableImage.create(8, 8, BufferedImage.TYPE_INT_ARGB)
         for (y in 0 until 8) {
            for (x in 0 until 8) {
               val color = RGBColor(x * 30, y * 30, 128, 255)
               image1.setColor(x, y, color)
               image2.setColor(x, y, color)
            }
         }
         // Differ on exactly one pixel so image2 is distinguishable
         image2.setColor(3, 3, RGBColor(255, 0, 0, 255))

         val snap1: IntArray = image1.pixels().map { it.argb }.toIntArray()
         val snap2: IntArray = image2.pixels().map { it.argb }.toIntArray()

         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(image1)
         stream.writeFrame(image2)
         stream.close()

         image1.pixels().map { it.argb }.toIntArray().toList() shouldBe snap1.toList()
         image2.pixels().map { it.argb }.toIntArray().toList() shouldBe snap2.toList()
      }
   }

})

fun disposeMethodTests(resourcePath: String) = wordSpec {
   val originalGif = AnimatedGifReader.read(ImageSource.of(javaClass.getResourceAsStream(resourcePath)))

   "StreamingGifWriter" should {
      val writer = StreamingGifWriter()
      val output = ByteArrayOutputStream()
      val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)

      for ((i, frame) in originalGif.frames.withIndex()) {
         "write frame with index \"$i\" to stream with \"${originalGif.getDisposeMethod(i)}\" dispose method" {
            stream.writeFrame(frame, originalGif.getDelay(i), originalGif.getDisposeMethod(i))
         }
      }

      "close" {
         stream.close()
      }

      "contents from $resourcePath and output should be equal" {
         val outputGif = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         for ((i, frame) in outputGif.frames.withIndex()) {
            outputGif.getDisposeMethod(i) shouldBe originalGif.getDisposeMethod(i)
            outputGif.getDelay(i) shouldBe originalGif.getDelay(i)
            frame.pixels() shouldBe originalGif.getFrame(i).pixels()
         }
      }
   }
}
