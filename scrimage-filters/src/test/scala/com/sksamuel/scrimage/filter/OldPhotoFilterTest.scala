package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class OldPhotoFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  private val original = ImmutableImage.fromStream(getClass.getResourceAsStream("/bird_small.png"))
  private val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/filters/bird_small_oldphoto.png"))

  ignore("oldphoto filter output matches expected") {
    assert(original.filter(new OldPhotoFilter) === expected)
  }
}
