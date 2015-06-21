package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.GifSequenceWriter
import org.apache.commons.io.IOUtils
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration._

class GifSequenceWriterTest extends WordSpec with Matchers {

  val bird = Image.fromResource("/bird_small.png")

  "gif sequence writer" should {
    "write all frames" in {
      val frames = Seq(bird, bird.flipX, bird.flipY, bird.flipX.flipY)
      val actual = GifSequenceWriter().withFrameDelay(500.millis).bytes(frames)
      actual shouldBe IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/nio/animated_birds.gif"))
    }
  }
}
