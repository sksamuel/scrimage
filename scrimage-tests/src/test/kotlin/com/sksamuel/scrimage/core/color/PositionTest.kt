package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.Position
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PositionTest : FunSpec({

   test("centre positions image in the centre of the canvas") {
      20 shouldBe Position.Center.calculateX(100, 100, 60, 80)
      10 shouldBe Position.Center.calculateY(100, 100, 60, 80)
   }

   test("centre left positions image at (0,center) of the canvas") {
      0 shouldBe Position.CenterLeft.calculateX(100, 100, 60, 80)
      10 shouldBe Position.CenterLeft.calculateY(100, 100, 60, 80)
   }

   test("centre right positions image at (image-width,centre) of the canvas") {
      40 shouldBe Position.CenterRight.calculateX(100, 100, 60, 80)
      10 shouldBe Position.CenterRight.calculateY(100, 100, 60, 80)
   }

   test("top left positions image at (0,0) of the canvas") {
      0 shouldBe Position.TopLeft.calculateX(100, 100, 60, 80)
      0 shouldBe Position.TopLeft.calculateY(100, 100, 60, 80)
   }

   test("top center positions image at (center,top) of the canvas") {
      20 shouldBe Position.TopCenter.calculateX(100, 100, 60, 80)
      0 shouldBe Position.TopCenter.calculateY(100, 100, 60, 80)
   }

   test("top right positions image at (image-width,0) of the canvas") {
      40 shouldBe Position.TopRight.calculateX(100, 100, 60, 80)
      0 shouldBe Position.TopRight.calculateY(100, 100, 60, 80)
   }

   test("bottom left positions image at (0,0) of the canvas") {
      0 shouldBe Position.BottomLeft.calculateX(100, 100, 60, 80)
      20 shouldBe Position.BottomLeft.calculateY(100, 100, 60, 80)
   }

   test("bottom center positions image at (center,bottom) of the canvas") {
      20 shouldBe Position.BottomCenter.calculateX(100, 100, 60, 80)
      20 shouldBe Position.BottomCenter.calculateY(100, 100, 60, 80)
   }

   test("bottom right positions image at (right,centre) of the canvas") {
      40 shouldBe Position.BottomRight.calculateX(100, 100, 60, 80)
      20 shouldBe Position.BottomRight.calculateY(100, 100, 60, 80)
   }

})
