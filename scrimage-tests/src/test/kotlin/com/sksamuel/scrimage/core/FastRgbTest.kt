package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.pixels.FastRgb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.ByteArrayOutputStream
import kotlin.random.Random

/**
 * FastRgb must be bit-identical to BufferedImage.getRGB / setRGB, both in the returned
 * ARGB ints and in the raw raster contents it writes, for every standard image type
 * and for sub-raster (getSubimage) views.
 */
class FastRgbTest : FunSpec({

   val types = (1..13).toList()
   val w = 211
   val h = 97

   fun raw(b: BufferedImage): ByteArray {
      val r: Raster = b.raster
      val bos = ByteArrayOutputStream()
      for (y in 0 until b.height) {
         when (val row = r.getDataElements(0, y, b.width, 1, null)) {
            is IntArray -> row.forEach { v -> bos.write(v ushr 24); bos.write(v ushr 16); bos.write(v ushr 8); bos.write(v) }
            is ByteArray -> bos.write(row)
            is ShortArray -> row.forEach { v -> bos.write(v.toInt() ushr 8); bos.write(v.toInt()) }
         }
      }
      return bos.toByteArray()
   }

   fun randomArgb(n: Int, seed: Int): IntArray {
      val rnd = Random(seed)
      val data = IntArray(n) { rnd.nextInt() }
      // make sure every alpha value, including 0, is present
      for (a in 0 until 256) data[a] = (a shl 24) or (rnd.nextInt() and 0xFFFFFF)
      return data
   }

   fun image(type: Int): BufferedImage {
      val img = BufferedImage(w, h, type)
      img.setRGB(0, 0, w, h, randomArgb(w * h, type), 0, w)
      return img
   }

   for (type in types) {
      for (sub in listOf(false, true)) {
         val label = "type $type" + if (sub) " sub-raster" else ""

         test("getRGB matches BufferedImage.getRGB for $label") {
            val base = image(type)
            val img = if (sub) base.getSubimage(7, 5, 150, 60) else base
            FastRgb.getRGB(img).toList() shouldBe img.getRGB(0, 0, img.width, img.height, null, 0, img.width).toList()
            FastRgb.getRGB(img, 3, 4, 40, 20).toList() shouldBe img.getRGB(3, 4, 40, 20, null, 0, 40).toList()
         }

         test("setRGB matches BufferedImage.setRGB (ints and raw raster) for $label") {
            val p1 = image(type)
            val p2 = image(type)
            val s1 = if (sub) p1.getSubimage(7, 5, 150, 60) else p1
            val s2 = if (sub) p2.getSubimage(7, 5, 150, 60) else p2
            val data = randomArgb(s1.width * s1.height, 1000 + type)
            s1.setRGB(0, 0, s1.width, s1.height, data, 0, s1.width)
            FastRgb.setRGB(s2, data)
            s1.setRGB(3, 4, 40, 20, data, 0, 40)
            FastRgb.setRGB(s2, 3, 4, 40, 20, data)
            raw(p2).toList() shouldBe raw(p1).toList()
            p2.getRGB(0, 0, w, h, null, 0, w).toList() shouldBe p1.getRGB(0, 0, w, h, null, 0, w).toList()
         }
      }
   }
})
