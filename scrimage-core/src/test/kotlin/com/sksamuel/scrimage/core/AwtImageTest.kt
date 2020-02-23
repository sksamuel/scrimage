package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AwtImageTest : FunSpec({

   test("AwtImage.points return array of points") {
      val image = ImmutableImage.create(6, 4)
      image.points().toList() shouldBe listOf(
         java.awt.Point(0, 0),
         java.awt.Point(1, 0),
         java.awt.Point(2, 0),
         java.awt.Point(3, 0),
         java.awt.Point(4, 0),
         java.awt.Point(5, 0),
         java.awt.Point(0, 1),
         java.awt.Point(1, 1),
         java.awt.Point(2, 1),
         java.awt.Point(3, 1),
         java.awt.Point(4, 1),
         java.awt.Point(5, 1),
         java.awt.Point(0, 2),
         java.awt.Point(1, 2),
         java.awt.Point(2, 2),
         java.awt.Point(3, 2),
         java.awt.Point(4, 2),
         java.awt.Point(5, 2),
         java.awt.Point(0, 3),
         java.awt.Point(1, 3),
         java.awt.Point(2, 3),
         java.awt.Point(3, 3),
         java.awt.Point(4, 3),
         java.awt.Point(5, 3)
      )
   }
})
