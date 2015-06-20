package com.sksamuel.scrimage.filter

import java.awt.image.BufferedImage

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class SummerFilterTest extends FunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  val original = Image.fromResource("/bird_small.png", BufferedImage.TYPE_INT_ARGB)

  test("summer filter output matches expected") {
    val expected = Image.fromResource("/com/sksamuel/scrimage/filters/bird_small_summer.png")
    val actual = original.filter(SummerFilter())
    assert(actual === expected)
  }
}
