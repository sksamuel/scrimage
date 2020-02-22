package com.sksamuel.scrimage

import org.scalatest.{FlatSpec, Matchers}

class AwtImageTest extends FlatSpec with Matchers {

  "AwtImage.points" should "return array of points" in {
    val image = ImmutableImage.apply(6, 4)
    image.points().toList shouldBe
      List(new java.awt.Point(0, 0),  new java.awt.Point(1, 0),  new java.awt.Point(2, 0),  new java.awt.Point(3, 0),  new java.awt.Point(4, 0),  new java.awt.Point(5, 0),  new java.awt.Point(0, 1),  new java.awt.Point(1, 1),  new java.awt.Point(2, 1),  new java.awt.Point(3, 1),  new java.awt.Point(4, 1),  new java.awt.Point(5, 1),  new java.awt.Point(0, 2),  new java.awt.Point(1, 2),  new java.awt.Point(2, 2),  new java.awt.Point(3, 2),  new java.awt.Point(4, 2),  new java.awt.Point(5, 2),  new java.awt.Point(0, 3),  new java.awt.Point(1, 3),  new java.awt.Point(2, 3),  new java.awt.Point(3, 3),  new java.awt.Point(4, 3),  new java.awt.Point(5,3))
  }
}
