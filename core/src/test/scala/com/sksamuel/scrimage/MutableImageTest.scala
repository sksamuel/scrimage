package com.sksamuel.scrimage

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }

/** @author Stephen Samuel */
class MutableImageTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val in = getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")
  val image = MutableImage(in)

  test("when flipping on x axis a new image is not created") {
    val flipped = image.flipX
    assert(flipped.eq(image))
  }

  test("when flipping on y axis a new image is not created") {
    val flipped = image.flipY
    assert(flipped.eq(image))
  }

  test("when filling an image then a new image is not created") {
    assert(image.eq(image.filled(0)))
    assert(image.eq(image.filled(java.awt.Color.BLACK)))
  }

  test("when creating a mutable copy of an existing image then the underlying image is copied") {
    val image = Image.empty(200, 300)
    assert(image.awt.hashCode === image.awt.hashCode)
    assert(image.toMutable.awt.hashCode != image.awt.hashCode)

    assert(image.awt.hashCode === image.awt.hashCode)
    assert(MutableImage(image).awt.hashCode != image.awt.hashCode)
  }
}
