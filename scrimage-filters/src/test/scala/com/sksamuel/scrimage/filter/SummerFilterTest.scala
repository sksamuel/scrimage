package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class SummerFilterTest extends FunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  test("summer filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_summer.png"))
    val actual = original.filter(SummerFilter())
    assert(actual === expected)
  }
}
