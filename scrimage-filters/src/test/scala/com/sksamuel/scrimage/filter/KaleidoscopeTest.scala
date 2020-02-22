package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class KaleidoscopeTest extends FunSuite with Matchers {

  implicit val writer: PngWriter = PngWriter.MaxCompression
  private val bird = ImmutableImage.fromResource("/bird_small.png")
  private val love = ImmutableImage.fromResource("/love.jpg")
  private val masks = ImmutableImage.fromResource("/masks.jpg")

  test("kaleidoscope filter output matches expected") {
    bird.filter(new KaleidoscopeFilter()) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/bird_small_kaleidoscope.png")
    love.filter(new KaleidoscopeFilter()) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/love_kaleidoscope.png")
    masks.filter(new KaleidoscopeFilter()) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_kaleidoscope.png")
  }

  test("kaleidoscope filter should support sides") {
    masks.filter(new KaleidoscopeFilter(4)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_4.png")
    masks.filter(new KaleidoscopeFilter(5)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_5.png")
    masks.filter(new KaleidoscopeFilter(6)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_6.png")
  }

  test("kaleidoscope filter should error if setting sides < 3") {
    intercept[RuntimeException] {
      new KaleidoscopeFilter(2)
    }
  }
}
