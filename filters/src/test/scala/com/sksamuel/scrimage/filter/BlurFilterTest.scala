package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

/** @author Stephen Samuel */
class BlurFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = getClass.getResourceAsStream("/bird_small.png")
  val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_blur.png")

  test("filter output matches expected") {
    assert(Image(original).filter(BlurFilter) == Image(expected))
  }

  test("jpeg reader"){
    import com.sksamuel.scrimage.Format
    val champi = Image(getClass.getResourceAsStream("/champi.jpg"))
    val ch = champi.scaleTo(300, 400)
    ch.write("champi.png", Format.PNG)
  }
}
