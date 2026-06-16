package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.color.RGBColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class ResizeTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("enlarging a canvas with TopLeft should position the image to the left and top") {
      val scaled = image.scaleTo(100, 100)
      val resized = scaled.resizeTo(200, 200, Position.TopLeft)
      resized.width shouldBe 200
      resized.height shouldBe 200
      for (x in 0..199) {
         for (y in 0..199) {
            if (x in 0..99 && y in 0..99)
               scaled.pixel(x, y) shouldBe resized.pixel(x, y)
            else
               resized.pixel(x, y).toARGBInt() shouldBe RGBColor(0, 0, 0, 0).toARGBInt()
         }
      }
   }

   test("enlarging a canvas with BottomRight should position the image to the bottom and to the right") {
      val scaled = image.scaleTo(100, 100)
      scaled.width shouldBe 100
      scaled.height shouldBe 100
      scaled.count() shouldBe 100 * 100
      val resized = scaled.resizeTo(200, 200, Position.BottomRight)
      resized.width shouldBe 200
      resized.height shouldBe 200
      resized.count() shouldBe 200 * 200
      for (x in 0..199) {
         for (y in 0..199) {
            if (x in 100..199 && y in 100..199)
               scaled.pixel(x - 100, y - 100).toARGBInt() shouldBe resized.pixel(x, y).toARGBInt()
            else
               resized.pixel(x, y).toARGBInt() shouldBe RGBColor(0, 0, 0, 0).toARGBInt()
         }
      }
   }

   test("enlarging a canvas with TopRight should position the image to the top and to the right") {
      val scaled = image.scaleTo(100, 100)
      val resized = scaled.resizeTo(200, 200, Position.TopRight)
      resized.width shouldBe 200
      resized.height shouldBe 200
      for (x in 0..199) {
         for (y in 0..199) {
            if (x in 100..199 && y in 0..99)
               scaled.pixel(x - 100, y).toARGBInt() shouldBe resized.pixel(x, y).toARGBInt()
            else
               resized.pixel(x, y).toARGBInt() shouldBe RGBColor(0, 0, 0, 0).toARGBInt()
         }
      }
   }

   test("enlarging a canvas with Centre should position the image in the center") {
      val scaled = image.scaleTo(100, 100)
      val resized = scaled.resizeTo(200, 200, Position.Center)
      resized.width shouldBe 200
      resized.height shouldBe 200
      for (x in 0..199) {
         for (y in 0..199) {
            if (x in 50..149 && y in 50..149)
               scaled.pixel(x - 50, y - 50).toARGBInt() shouldBe resized.pixel(x, y).toARGBInt()
            else
               resized.pixel(x, y).toARGBInt() shouldBe RGBColor(0, 0, 0, 0).toARGBInt()
         }
      }
   }

   test("when enlarging the background should be set to the specified color parameter") {
      val scaled = image.scaleTo(100, 100)
      val resized = scaled.resizeTo(200, 200, Position.Center, Color.BLUE)
      for (x in 0..199) {
         for (y in 0..199) {
            if (x in 50..149 && y in 50..149)
               scaled.pixel(x - 50, y - 50).toARGBInt() shouldBe resized.pixel(x, y).toARGBInt()
            else
               resized.pixel(x, y).toARGBInt() shouldBe RGBColor(0, 0, 255, 255).toARGBInt()
         }
      }
   }

   test("when resizing an image the output image should have specified dimensions") {
      val r = image.resizeTo(900, 300)
      900 shouldBe r.width
      300 shouldBe r.height
   }

   test("when resizing an image to the ratio the output image should have specified dimensions") {
      val srcRatio = image.ratio()
      val actual1 = image.resizeToRatio(srcRatio)
      val actual2 = image.resizeToRatio(1.0)
      val actual3 = image.resizeToRatio(2.0)
      actual1 shouldBe image
      actual2.ratio() shouldBe 1.0
      actual3.ratio() shouldBe 2.0
   }

   test("resizeToRatio should throw IllegalArgumentException for a zero ratio") {
      shouldThrow<IllegalArgumentException> {
         image.resizeToRatio(0.0)
      }
   }

   test("resizeToRatio should throw IllegalArgumentException for a negative ratio") {
      shouldThrow<IllegalArgumentException> {
         image.resizeToRatio(-0.5)
      }
   }

   test("resizeToRatio should throw IllegalArgumentException for a NaN ratio") {
      shouldThrow<IllegalArgumentException> {
         image.resizeToRatio(Double.NaN)
      }
   }

   test("when resizing by pixels then the output image has the given dimensions") {
      val scaled = image.resizeTo(440, 505)
      440 shouldBe scaled.width
      505 shouldBe scaled.height
   }

   test("when resizing by scale factor then the output image has the scaled dimensions") {
      val scaled = image.resize(0.5)
      972 shouldBe scaled.width
      648 shouldBe scaled.height
   }

   test("resizeToWidth clamps the derived height to at least 1 for an extreme aspect ratio") {
      // A 1000x1 strip: the aspect-preserving height is 1000/1000 -> floors to 0, which used
      // to throw "Width (10) and height (0) cannot be <= 0" creating a 0-height canvas.
      val out = ImmutableImage.create(1000, 1).resizeToWidth(10)
      out.width shouldBe 10
      out.height shouldBe 1
   }

   test("resizeToHeight clamps the derived width to at least 1 for an extreme aspect ratio") {
      val out = ImmutableImage.create(1, 1000).resizeToHeight(10)
      out.width shouldBe 1
      out.height shouldBe 10
   }
})
