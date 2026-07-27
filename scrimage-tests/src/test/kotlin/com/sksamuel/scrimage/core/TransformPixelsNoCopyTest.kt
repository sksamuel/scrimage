package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.AverageGrayscale
import com.sksamuel.scrimage.color.GrayscaleMethod
import com.sksamuel.scrimage.color.LumaGrayscale
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.color.WeightedGrayscale
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * map/toGrayscale/contrast/removeTransparency used to `copy()` the whole raster
 * and then mutate the copy in place, even though the in-place op bulk-writes every
 * pixel — making the initial raster copy pure overhead. They now read the pixels
 * from the source and bulk-write the transformed values into a blank image of the
 * same type, which must be bit-identical to the old copy-then-mutate path.
 *
 * Palette-based images (IndexColorModel) must keep the copy() based path: a blank
 * indexed image would get the default palette, not the source's palette.
 *
 * These tests pin the new implementations against the old path, which is still
 * expressible as copy() + the (unchanged) MutableImage *InPlace methods.
 */
class TransformPixelsNoCopyTest : FunSpec({

   fun argbOf(image: ImmutableImage): List<Int> =
      image.awt().getRGB(0, 0, image.width, image.height, null, 0, image.width).toList()

   // an ARGB image with varied colours and varied alpha (incl 0, awkward values, 255)
   fun argbFixture(): ImmutableImage {
      val w = 13
      val h = 7
      val pixels = Array(w * h) { i ->
         val x = i % w
         val y = i / w
         Pixel(x, y, (x * 37 + 11) % 256, (y * 53 + 5) % 256, (x * y * 29) % 256, intArrayOf(0, 1, 7, 64, 128, 200, 255)[i % 7])
      }
      return ImmutableImage.create(w, h, pixels)
   }

   // an indexed (palette-based) image
   fun indexedFixture(): ImmutableImage {
      val buf = BufferedImage(9, 5, BufferedImage.TYPE_BYTE_INDEXED)
      for (y in 0 until buf.height) for (x in 0 until buf.width) {
         buf.setRGB(x, y, java.awt.Color((x * 40) % 256, (y * 60) % 256, (x + y) * 17 % 256).rgb)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   val mapper: (Pixel) -> Color = { p ->
      Color((p.x() * 19 + p.red()) % 256, (p.y() * 31 + p.green()) % 256, 255 - p.blue(), p.alpha())
   }

   fun oldToGrayscale(image: ImmutableImage, method: GrayscaleMethod): ImmutableImage {
      val target = image.copy()
      val argb = target.awt().getRGB(0, 0, target.width, target.height, null, 0, target.width)
      for (i in argb.indices) {
         val p = argb[i]
         val gray = method.toGrayscale(RGBColor((p shr 16) and 0xFF, (p shr 8) and 0xFF, p and 0xFF, (p ushr 24) and 0xFF))
         argb[i] = (gray.alpha shl 24) or (gray.gray shl 16) or (gray.gray shl 8) or gray.gray
      }
      target.awt().setRGB(0, 0, target.width, target.height, argb, 0, target.width)
      return target
   }

   listOf(
      "ARGB" to ::argbFixture,
      "indexed" to ::indexedFixture
   ).forEach { (name, fixture) ->

      test("map matches the old copy-then-mapInPlace path ($name)") {
         val image = fixture()
         val old = image.copy()
         old.mapInPlace(mapper)
         val new = image.map(mapper)
         new.awt().type shouldBe old.awt().type
         argbOf(new) shouldBe argbOf(old)
      }

      test("contrast matches the old copy-then-contrastInPlace path ($name)") {
         val image = fixture()
         for (factor in listOf(0.0, 0.5, 1.3, 2.0)) {
            val old = image.copy()
            old.contrastInPlace(factor)
            argbOf(image.contrast(factor)) shouldBe argbOf(old)
         }
      }

      test("removeTransparency matches the old copy-then-replaceTransparencyInPlace path ($name)") {
         val image = fixture()
         val color = Color(10, 200, 30, 255)
         val old = image.copy()
         old.replaceTransparencyInPlace(color)
         argbOf(image.removeTransparency(color)) shouldBe argbOf(old)
      }

      test("toGrayscale matches the old copy-based path ($name)") {
         val image = fixture()
         for (method in listOf(LumaGrayscale(), AverageGrayscale(), WeightedGrayscale())) {
            argbOf(image.toGrayscale(method)) shouldBe argbOf(oldToGrayscale(image, method))
         }
      }
   }

   test("the source image is not mutated by map/contrast/removeTransparency/toGrayscale") {
      val image = argbFixture()
      val before = argbOf(image)
      image.map(mapper)
      image.contrast(1.7)
      image.removeTransparency(Color.WHITE)
      image.toGrayscale(WeightedGrayscale())
      argbOf(image) shouldBe before
   }

   test("indexed images keep their type and palette (guard against blank() default palette)") {
      val image = indexedFixture()
      val result = image.map { p -> p.toColor().awt() } // identity map
      result.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      argbOf(result) shouldBe argbOf(image.copy())
   }
})
