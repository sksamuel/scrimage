package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SplitChannelsFilterTest : FunSpec({

   val pixels = arrayOf(Pixel(0, 0, 200, 150, 100, 220))
   val image = ImmutableImage.create(1, 1, pixels)

   test("keeps only the red channel when red is the only true flag") {
      val p = image.filter(SplitChannelsFilter(true, false, false)).pixel(0, 0)
      p.red() shouldBe 200
      p.green() shouldBe 0
      p.blue() shouldBe 0
      p.alpha() shouldBe 220
   }

   test("keeps only green and blue when red is false") {
      val p = image.filter(SplitChannelsFilter(false, true, true)).pixel(0, 0)
      p.red() shouldBe 0
      p.green() shouldBe 150
      p.blue() shouldBe 100
      p.alpha() shouldBe 220
   }

   test("all true leaves the colour channels unchanged") {
      val p = image.filter(SplitChannelsFilter(true, true, true)).pixel(0, 0)
      p.red() shouldBe 200
      p.green() shouldBe 150
      p.blue() shouldBe 100
      p.alpha() shouldBe 220
   }

   test("all false zeroes the colour channels but keeps alpha") {
      val p = image.filter(SplitChannelsFilter(false, false, false)).pixel(0, 0)
      p.red() shouldBe 0
      p.green() shouldBe 0
      p.blue() shouldBe 0
      p.alpha() shouldBe 220
   }
})
