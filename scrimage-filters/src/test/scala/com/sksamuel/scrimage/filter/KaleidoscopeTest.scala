package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class KaleidoscopeTest extends FunSuite with Matchers {

  implicit val writer: PngWriter = PngWriter.MaxCompression
  private val bird = Image.fromResource("/bird_small.png")
  private val love = Image.fromResource("/love.jpg")
  private val masks = Image.fromResource("/masks.jpg")

  test("kaleidoscope filter output matches expected") {
    bird.filter(new KaleidoscopeFilter()) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/bird_small_kaleidoscope.png")
    love.filter(new KaleidoscopeFilter()) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/love_kaleidoscope.png")
    masks.filter(new KaleidoscopeFilter()) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_kaleidoscope.png")
  }

  test("kaleidoscope filter should support sides") {
    masks.filter(new KaleidoscopeFilter(4)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_4.png")
    masks.filter(new KaleidoscopeFilter(5)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_5.png")
    masks.filter(new KaleidoscopeFilter(6)) shouldBe Image.fromResource("/com/sksamuel/scrimage/filters/kaleidoscope/masks_6.png")
  }

  test("kaleidoscope filter should error if setting sides < 3") {
    intercept[RuntimeException] {
      new KaleidoscopeFilter(2)
    }
  }
}
