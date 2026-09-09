package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Tests for histogram() (inherited from AwtImage) and toSquare().
 */
class ImageHistogramSquareTest : FunSpec({

   test("histogram counts every pixel per channel") {
      val pixels = arrayOf(
         Pixel(0, 0, 0, 0, 0, 255),       // black
         Pixel(1, 0, 255, 255, 255, 255), // white
         Pixel(0, 1, 255, 0, 0, 255),     // red
         Pixel(1, 1, 0, 0, 255, 255)      // blue
      )
      val hist = ImmutableImage.create(2, 2, pixels).histogram()

      hist.red().sum() shouldBe 4L
      hist.red()[0] shouldBe 2L      // black + blue
      hist.red()[255] shouldBe 2L    // white + red

      hist.alpha()[255] shouldBe 4L
      hist.luminance().sum() shouldBe 4L
   }

   test("histogram getters return defensive copies") {
      val hist = ImmutableImage.create(1, 1).histogram()
      val before = hist.red()[0]
      hist.red()[0] = 999L                 // mutate the returned array
      hist.red()[0] shouldBe before        // a fresh call is unaffected
   }

   test("toSquare pads the shorter side and centres the image") {
      val squared = ImmutableImage.filled(4, 2, Color.RED).toSquare(Color.BLACK)
      squared.width shouldBe 4
      squared.height shouldBe 4
      // original red band centred vertically (rows 1..2), black padding top and bottom
      squared.pixel(0, 0).toARGBInt() shouldBe Color.BLACK.rgb
      squared.pixel(0, 1).toARGBInt() shouldBe Color.RED.rgb
      squared.pixel(0, 3).toARGBInt() shouldBe Color.BLACK.rgb
   }

   test("toSquare leaves an already-square image the same size") {
      val squared = ImmutableImage.create(5, 5).toSquare()
      squared.width shouldBe 5
      squared.height shouldBe 5
   }
})
