@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
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
   include(disposeMethodTests( "/com/sksamuel/scrimage/nio/animated_birds.gif")) // tests 'doNotDispose'
   include(disposeMethodTests( "/com/sksamuel/scrimage/nio/bananaDance.gif")) // tests 'restoreToBackgroundColor'
   include(disposeMethodTests( "/com/sksamuel/scrimage/nio/canvas_prev.gif")) // tests 'restoreToPrevious'

   val bird = ImmutableImage.loader().fromResource("/bird_small.png")
   "StreamingGifWriter" should {
      "write to memory"  {
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
         for((i, frame) in outputGif.frames.withIndex()) {
            outputGif.getDisposeMethod(i) shouldBe originalGif.getDisposeMethod(i)
            outputGif.getDelay(i) shouldBe originalGif.getDelay(i)
            frame.pixels() shouldBe originalGif.getFrame(i).pixels()
         }
      }
   }
}
