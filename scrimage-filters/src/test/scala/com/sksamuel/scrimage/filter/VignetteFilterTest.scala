package com.sksamuel.scrimage.filter

import java.io.File

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.{ Format, Image }

/** @author Stephen Samuel */
class VignetteFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  ignore("filter output matches expected") {
    val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_vignette.png"))
    val actual = original.filter(VignetteFilter())
    actual.write(new File("vignette.png"), Format.PNG)
    assert(actual === expected)
  }
}
