package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class PixelateFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  implicit private val writer: PngWriter = PngWriter.MaxCompression

  val original = Image.fromStream(getClass.getResourceAsStream("/bird_small.png"))
  val expected2 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_block_2.png")
  val expected4 = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_block_4.png")

  test("pixelate filter output matches expected") {
    assert(original.filter(new PixelateFilter(2)) === Image.fromStream(expected2))
    assert(original.filter(new PixelateFilter(4)) === Image.fromStream(expected4))
  }
}
