package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

// Both non-base-of-two
private const val width = (1024 + 7)
private const val height = (1536 + 29)

private val colorsUsed = listOf(
   Color.MAGENTA,
   Color.RED,
   Color.YELLOW,
   Color.ORANGE,
   Color.WHITE,
   Color.BLACK,
   Color.BLUE,
   Color.GRAY,
   Color.GREEN,
   Color.DARK_GRAY,
   Color.LIGHT_GRAY
)

private val colorsUnused = listOf(
   Color.PINK,
   Color.CYAN
)

private val baseImage = run {
   val type = BufferedImage.TYPE_INT_ARGB
   val image = BufferedImage(width, height, type)
   var colorIndex = 0
   (0.until(width)).forEach { x ->
      (0.until(height)).forEach { y ->
         image.setRGB(x, y, colorsUsed[colorIndex++].rgb)
         if (colorIndex == colorsUsed.size) {
            colorIndex = 0
         }
      }
   }
   image
}

private fun Pixel.matches(image: BufferedImage) {
   val alpha = this.alpha()
   val r = this.red()
   val g = this.green()
   val b = this.blue()

   val baseRgb = image.getRGB(this.x, this.y)
   val baseAlpha = baseRgb.ushr(24)
   val baseR = baseRgb.and(0x00FF0000).ushr(16)
   val baseG = baseRgb.and(0x0000FF00).ushr(8)
   val baseB = baseRgb.and(0x000000FF)

   alpha.shouldBe(baseAlpha)
   r.shouldBe(baseR)
   g.shouldBe(baseG)
   b.shouldBe(baseB)
}

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

   test("forEach") {
      val image = ImmutableImage.fromAwt(baseImage)
      image.forEach { p ->
         p.matches(baseImage)
      }
   }

   test("pixels") {
      val image = ImmutableImage.fromAwt(baseImage)
      image.pixels().forEach { p ->
         p.matches(baseImage)
      }
   }

   test("contains") {
      val image = ImmutableImage.fromAwt(baseImage)
      colorsUnused.map { c ->
         c to image.contains(c)
      }.shouldContainExactly(
         colorsUnused.map { it to false }
      )

      colorsUsed.map { c ->
         c to image.contains(c)
      }.shouldContainExactly(
         colorsUsed.map { it to true }
      )
   }

   test("exists") {
      val image = ImmutableImage.fromAwt(baseImage)

      image.exists { p ->
         p.x > width || p.y > height
      }.shouldBeFalse()

      image.exists { p ->
         p.x == (width / 2) && p.y == (height / 2)
      }.shouldBeTrue()
   }

   test("forAll") {
      val image = ImmutableImage.fromAwt(baseImage)

      image.forAll { it.x < width && it.y < height }.shouldBeTrue()
      image.forAll { it.x < width - 1 && it.y < height -1 }.shouldBeFalse()
   }

   test("forEachPixel") {
      val image = ImmutableImage.fromAwt(baseImage)
      val iter = image.iterator()

      image.forEachPixel { x, y, argb ->
         val refPixel = iter.next()
         x.shouldBe(refPixel.x)
         y.shouldBe(refPixel.y)
         argb.shouldBe(refPixel.argb)
      }
      iter.hasNext().shouldBeFalse()
   }

})
