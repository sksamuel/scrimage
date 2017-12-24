package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.Image
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}

class GifSequenceWriterTest extends WordSpec with Matchers {

  private val bird = Image.fromResource("/bird_small.png")

  "gif sequence writer" should {
    "write all frames" in {
      val frames = Seq(bird, bird.flipX, bird.flipY, bird.flipX.flipY)
      val actual = new GifSequenceWriter().withFrameDelay(500).bytes(frames.toArray)
      actual shouldBe IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/nio/animated_birds.gif"))
    }
  }
}
