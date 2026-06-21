@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifSequenceWriter
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.io.IOUtils
import java.awt.Color

class GifSequenceWriterTest : WordSpec({

   val bird = ImmutableImage.loader().fromResource("/bird_small.png")

   "gif sequence writer" should {
      "write all frames"  {
         val frames = listOf(bird, bird.flipX(), bird.flipY(), bird.flipX().flipY())
         val actual = GifSequenceWriter().withFrameDelay(500).bytes(frames.toTypedArray())
         actual shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/animated_birds.gif"))
      }
      "not leak ImageWriter on repeated calls" {
         val frames = arrayOf(bird)
         repeat(100) {
            GifSequenceWriter().bytes(frames)
         }
      }
      "write frames smaller than the output buffer size hint floor" {
         // The output buffer is pre-sized from the first frame's pixel count,
         // clamped to a minimum of 1024; a tiny image must still round-trip.
         val tiny1 = ImmutableImage.filled(2, 2, Color.RED)
         val tiny2 = ImmutableImage.filled(2, 2, Color.BLUE)
         val bytes = GifSequenceWriter().withFrameDelay(100).bytes(arrayOf(tiny1, tiny2))
         val decoded = AnimatedGifReader.read(ImageSource.of(bytes))
         decoded.frameCount shouldBe 2
         decoded.getFrame(0).width shouldBe 2
         decoded.getFrame(0).height shouldBe 2
         decoded.getFrame(1).width shouldBe 2
         decoded.getFrame(1).height shouldBe 2
      }
      "reject an empty frame array with a clear error" {
         // Previously dereferenced images[0] and threw a confusing ArrayIndexOutOfBoundsException.
         shouldThrow<IllegalArgumentException> {
            GifSequenceWriter().bytes(emptyArray())
         }
      }
   }

})
