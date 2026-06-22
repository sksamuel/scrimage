package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

class ZhangSkeletonTest : FunSpec({

   // Builds an image from a row-per-string grid: 'X' = black, anything else = white.
   fun img(rows: List<String>): ImmutableImage {
      val h = rows.size
      val w = rows[0].length
      val bi = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
      for (y in 0 until h)
         for (x in 0 until w)
            bi.setRGB(x, y, if (rows[y][x] == 'X') 0xff000000.toInt() else 0xffffffff.toInt())
      return ImmutableImage.wrapAwt(bi)
   }

   fun isBlack(image: ImmutableImage, x: Int, y: Int) = image.pixel(x, y).red() < 128

   fun blackCount(image: ImmutableImage): Int {
      var c = 0
      for (y in 0 until image.height)
         for (x in 0 until image.width)
            if (isBlack(image, x, y)) c++
      return c
   }

   test("a 1px line is a fixed point of thinning") {
      val out = img(
         listOf(
            ".........",
            "XXXXXXXXX",
            "........."
         )
      ).filter(ZhangSkeleton())
      for (x in 0 until 9) {
         isBlack(out, x, 0) shouldBe false
         isBlack(out, x, 1) shouldBe true
         isBlack(out, x, 2) shouldBe false
      }
   }

   test("a solid block is thinned but not erased") {
      val out = img((0 until 5).map { "XXXXX" }).filter(ZhangSkeleton())
      val c = blackCount(out)
      c shouldBeGreaterThan 0
      c shouldBeLessThan 25
   }

   test("a thick bar thins to a one-pixel-wide line") {
      // a 3-row-thick horizontal bar should reduce to a single row per column
      val out = img(
         listOf(
            ".........",
            "XXXXXXXXX",
            "XXXXXXXXX",
            "XXXXXXXXX",
            "........."
         )
      ).filter(ZhangSkeleton())
      // at most one black pixel in any column
      for (x in 0 until 9) {
         val col = (0 until 5).count { y -> isBlack(out, x, y) }
         col shouldBeLessThan 2
      }
      blackCount(out) shouldBeGreaterThan 0
   }

   test("an all-white image yields no skeleton") {
      blackCount(img(listOf(".....", ".....", ".....")).filter(ZhangSkeleton())) shouldBe 0
   }

   test("a single dark pixel is preserved (endpoint), not wiped") {
      val out = img(
         listOf(".....", ".....", "..X..", ".....", ".....")
      ).filter(ZhangSkeleton())
      isBlack(out, 2, 2) shouldBe true
      blackCount(out) shouldBe 1
   }

   // The old jhlabs SkeletonFilter never wrote the 1px border (it stayed
   // transparent black) and wiped images <= 2px to fully transparent.
   // ZhangSkeleton writes every pixel and is safe at any size.
   test("tiny images do not crash, preserve dimensions, and are fully opaque") {
      listOf(1 to 1, 1 to 5, 5 to 1, 2 to 2).forEach { (w, h) ->
         val out = ImmutableImage.filled(w, h, Color.BLACK).filter(ZhangSkeleton())
         out.width shouldBe w
         out.height shouldBe h
         for (y in 0 until h)
            for (x in 0 until w)
               out.pixel(x, y).alpha() shouldBe 255
      }
   }

   test("rejects out-of-range threshold") {
      shouldThrow<IllegalArgumentException> { ZhangSkeleton(-1) }
      shouldThrow<IllegalArgumentException> { ZhangSkeleton(256) }
   }
})
