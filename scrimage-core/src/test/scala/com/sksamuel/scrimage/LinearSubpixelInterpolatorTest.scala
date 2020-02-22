package com.sksamuel.scrimage

import org.scalatest.FunSuite

class LinearSubpixelInterpolatorTest extends FunSuite {

  private val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

  test("subpixel happy path") {
    assert(-1315602 === image.subpixel(2, 3))
    assert(-1381395 === image.subpixel(22, 63))
    assert(-2038553 === image.subpixel(152, 383))
  }

  test("subimage has right dimensions") {
    val covered = image.cover(200, 200)
    val subimage = covered.subimage(10, 10, 50, 100)
    assert(50 === subimage.width)
    assert(100 === subimage.height)
  }
}
