package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.pixels.PixelTools
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Point

class PixelToolsTest : FunSpec({

   val white: Int = 0xFFFFFFFF.toInt()
   val yellow: Int = 0xFFFFFF00.toInt()
   val pink: Int = 0xFFFFAFAF.toInt()

   test("non transparent alpha component") {
      white shouldBe java.awt.Color.WHITE.rgb
      PixelTools.alpha(white) shouldBe 255
   }

   test("transparent alpha component") {
      PixelTools.alpha(0xDD001122.toInt()) shouldBe 221
   }

   test("red component") {
      pink shouldBe java.awt.Color.PINK.rgb
      PixelTools.red(yellow) shouldBe 255
      PixelTools.red(pink) shouldBe 255
   }

   test("blue component") {
      pink shouldBe java.awt.Color.PINK.rgb
      PixelTools.blue(yellow) shouldBe 0
      PixelTools.blue(pink) shouldBe 175
   }

   test("green component") {
      yellow shouldBe java.awt.Color.YELLOW.rgb
      PixelTools.green(yellow) shouldBe 255
      PixelTools.green(pink) shouldBe 175
   }

   test("rgb combination") {
      PixelTools.rgb(255, 0, 255) shouldBe 0xFFFF00FF.toInt()
      PixelTools.rgb(85, 102, 119) shouldBe 0xFF556677.toInt()
   }

   test("coordinate to offset") {
      160 shouldBe PixelTools.coordsToOffset(10, 3, 50)
      10 shouldBe PixelTools.coordsToOffset(10, 0, 50)
      99 shouldBe PixelTools.coordsToOffset(49, 1, 50)
   }

   test("offset to coordinate") {
      PixelTools.offsetToPoint(0, 100) shouldBe Point(0, 0)
      PixelTools.offsetToPoint(100, 100) shouldBe Point(0, 1)
      PixelTools.offsetToPoint(99, 100) shouldBe Point(99, 0)
      PixelTools.offsetToPoint(199, 100) shouldBe Point(99, 1)
      PixelTools.offsetToPoint(101, 100) shouldBe Point(1, 1)
   }

})
