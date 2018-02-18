package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.FunSuite

class BlurFilterTest extends FunSuite {

  implicit val writer: PngWriter = PngWriter.MaxCompression
  private val folder = "/com/sksamuel/scrimage/filters/blur"

  test("filter output matches expected") {
    assert(Image.fromResource("/bird_small.png").filter(new BlurFilter) == Image.fromResource(s"$folder/bird_small_blur.png"))
    assert(Image.fromResource("/caviar.jpg").filter(new BlurFilter) == Image.fromResource(s"$folder/caviar_blur.png"))
    assert(Image.fromResource("/love.jpg").filter(new BlurFilter) == Image.fromResource(s"$folder/love_blur.png"))
    assert(Image.fromResource("/masks.jpg").filter(new BlurFilter) == Image.fromResource(s"$folder/masks_blur.png"))
  }
}
