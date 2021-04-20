@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.awt.Color

class PixelsTest : FunSpec({

   test("pixel returns correct ARGB integer") {
      val image = ImmutableImage.filled(50, 30, Color(0, 0, 0, 0))
      val g = image.awt().graphics
      g.color = Color.RED
      g.fillRect(10, 10, 10, 10)
      g.dispose()
      //image.updateFromAWT()
      image.pixel(0, 0).toARGBInt() shouldBe 0
      image.pixel(9, 10).toARGBInt() shouldBe 0
      image.pixel(10, 9).toARGBInt() shouldBe 0
      image.pixel(10, 10).toARGBInt() shouldBe 0xFFFF0000.toInt()
      image.pixel(19, 19).toARGBInt() shouldBe 0xFFFF0000.toInt()
      image.pixel(20, 20).toARGBInt() shouldBe 0
   }

   test("pixel array has correct number of pixels") {
      val image = ImmutableImage.filled(50, 30, Color(0, 0, 0, 0))
      1500 shouldBe image.pixels().size
   }

   test("pixel array has correct ARGB integer") {
      val image = ImmutableImage.filled(50, 30, Color(0, 0, 0, 0))
      val g = image.awt().graphics
      g.color = Color.RED
      g.fillRect(10, 10, 10, 10)
      g.dispose()
      // image.updateFromAWT()
      image.pixels()[0].toARGBInt() shouldBe 0
      image.pixels()[765].toARGBInt() shouldBe 0xFFFF0000.toInt()
   }

   test("argb returns array of ARGB bytes") {
      val image = ImmutableImage.filled(20, 20, Color.YELLOW)
      val components = image.argb()
      components.size shouldBe 400
      components.forAll {
         it shouldBe intArrayOf(255, 255, 255, 0)
      }
   }

   test("rgb returns array of RGB bytes") {
      val image = ImmutableImage.filled(20, 20, Color.YELLOW)
      val components = image.rgb()
      400 shouldBe components.size
      components.forAll {
         it shouldBe intArrayOf(255, 255, 0)
      }
   }

   test("argb pixel returns an array for the ARGB components") {
      val image = ImmutableImage.filled(20, 20, Color.YELLOW)
      val rgb = image.argb(10, 10)
      rgb shouldBe intArrayOf(255, 255, 255, 0)
   }

   test("rgb pixel returns an array for the RGB components") {
      val image = ImmutableImage.filled(20, 20, Color.YELLOW)
      val argb = image.rgb(10, 10)
      argb shouldBe intArrayOf(255, 255, 0)
   }

   test("pixel coordinate returns an ARGB integer for the pixel at that coordinate") {
      val image = ImmutableImage.filled(20, 20, Color.YELLOW)
      val pixel = image.pixel(10, 10)
      pixel.toARGBInt() shouldBe 0xFFFFFF00.toInt()
   }

   test("get pixel supports alpha") {
      val image = ImmutableImage.loader().fromResource("/balloon.png")
      image.pixel(0, 0).toARGBInt() shouldBe 0
      image.pixel(400, 400).toARGBInt() shouldBe 0xFFef5940.toInt()
   }

   test("pixels region") {
      val red = RGBColor(255, 0, 0)
      val blue = RGBColor(0, 0, 255)
      val striped = ImmutableImage.create(200, 100).map { p -> if (p.y % 2 == 0) red.awt() else blue.awt() }
      val pixels = striped.pixels(10, 10, 10, 10)
      for (k in 0..9) {
         pixels[k].toARGBInt() shouldBe red.toARGBInt()
      }
      for (k in 10..19) {
         pixels[k].toARGBInt() shouldBe blue.toARGBInt()
      }
      for (k in 20..29) {
         pixels[k].toARGBInt() shouldBe red.toARGBInt()
      }
      for (k in 30..39) {
         pixels[k].toARGBInt() shouldBe blue.toARGBInt()
      }
   }
})
