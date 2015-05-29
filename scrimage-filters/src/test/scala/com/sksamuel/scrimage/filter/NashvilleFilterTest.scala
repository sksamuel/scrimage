package com.sksamuel.scrimage.filter

import java.io.File

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import com.sksamuel.scrimage.{ Format, Image }

/** @author Stephen Samuel */
class NashvilleFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original = Image(getClass.getResourceAsStream("/bird_small.png"))

  ignore("filter output matches expected") {
    val expected = getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_nashville.png")
    val actual = original.filter(NashvilleFilter)
    actual.write(new File("nashville.png"), Format.PNG)
    assert(actual === Image(expected))
  }
}
