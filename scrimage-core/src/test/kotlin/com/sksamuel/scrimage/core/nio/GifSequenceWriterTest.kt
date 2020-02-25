@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifSequenceWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.io.IOUtils

class GifSequenceWriterTest : WordSpec({

   val bird = ImmutableImage.loader().fromResource("/bird_small.png")

   "gif sequence writer" should {
      "write all frames"  {
         val frames = listOf(bird, bird.flipX(), bird.flipY(), bird.flipX().flipY())
         val actual = GifSequenceWriter().withFrameDelay(500).bytes(frames.toTypedArray())
         actual shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/animated_birds.gif"))
      }
   }

})
