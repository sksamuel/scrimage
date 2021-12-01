@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.io.IOUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.Duration

class StreamingGifWriterTest : WordSpec({

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
