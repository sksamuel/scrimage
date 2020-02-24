package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.color.X11Colorlist
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PadTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")
   val chip = ImmutableImage.fromResource("/transparent_chip.png")

   test("padding should keep alpha") {
      chip
         .padWith(10, 20, 30, 40, RGBColor(100, 255, 50, 0).awt())
         .underlay(ImmutableImage.filled(1000, 1000, X11Colorlist.Firebrick.awt())) shouldBe
         ImmutableImage.fromResource("/com/sksamuel/scrimage/chip_pad.png")
   }

   test("when padding to a width smaller than the image width then the width is not reduced") {
      val blank = ImmutableImage.create(85, 56)
      val padded = blank.padTo(55, 162)
      85 shouldBe padded.width
   }

   test("when padding to a height smaller than the image height then the height is not reduced") {
      val blank = ImmutableImage.create(85, 56)
      val padded = blank.padTo(90, 15)
      56 shouldBe padded.height
   }

   test("when padding to a width larger than the image width then the width is increased") {
      val blank = ImmutableImage.create(85, 56)
      val padded = blank.padTo(151, 162)
      151 shouldBe padded.width
   }

   test("when padding to a height larger than the image height then the height is increased") {
      val blank = ImmutableImage.create(85, 56)
      val padded = blank.padTo(90, 77)
      77 shouldBe padded.height
   }

   test("when padding to a size larger than the image then the image canvas is increased") {
      val blank = ImmutableImage.create(85, 56)
      val padded = blank.padTo(515, 643)
      515 shouldBe padded.width
      643 shouldBe padded.height
   }

   test("when padding with a border size then the width and height are increased by the right amount") {
      val padded = image.pad(4, java.awt.Color.WHITE)
      1952 shouldBe padded.width
      1304 shouldBe padded.height
   }

})
