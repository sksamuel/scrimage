package com.sksamuel.scrimage.filter

import java.awt.Color

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{FunSuite, Matchers}

class BorderFilterTest extends FunSuite with Matchers {

  implicit val writer: PngWriter = PngWriter.MaxCompression
  private val bird = ImmutableImage.fromResource("/bird_small.png")
  private val love = ImmutableImage.fromResource("/love.jpg")
  private val masks = ImmutableImage.fromResource("/masks.jpg")

  test("filter output matches expected") {
    val expectedb = ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/bird_small_border.png")
    assert(bird.filter(new BorderFilter(8)) === expectedb)
  }

  test("should support multiple border levels") {
    masks.filter(new BorderFilter(2)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_2.png")
    masks.filter(new BorderFilter(6)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_6.png")
    masks.filter(new BorderFilter(13)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/masks_border_13.png")
  }

  test("should support multiple border colours") {
    love.filter(new BorderFilter(5, Color.GREEN)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_green.png")
    love.filter(new BorderFilter(5, Color.BLUE)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_blue.png")
    love.filter(new BorderFilter(5, Color.RED)) shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/filters/border/love_border_red.png")
  }
}
