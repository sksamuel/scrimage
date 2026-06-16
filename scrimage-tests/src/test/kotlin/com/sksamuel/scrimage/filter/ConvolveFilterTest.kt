package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class ConvolveFilterTest : FunSpec({

   test("identity kernel leaves the image unchanged") {
      val img = ImmutableImage.create(5, 5).map { p -> Color(p.x * 20 + 10, p.y * 20 + 10, 100) }
      val identity = floatArrayOf(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f)
      val out = img.filter(ConvolveFilter(identity))
      out.pixels().toList() shouldBe img.pixels().toList()
   }

   test("sharpen kernel applies the matrix as given") {
      // gray 100 everywhere except a brighter centre pixel
      val img = ImmutableImage.create(5, 5).map { p ->
         if (p.x == 2 && p.y == 2) Color(200, 200, 200) else Color(100, 100, 100)
      }
      val sharpen = floatArrayOf(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)
      val out = img.filter(ConvolveFilter(sharpen))
      // centre: 5*200 - 4*100 = 600 -> clamped to 255
      out.pixel(2, 2).red() shouldBe 255
      // orthogonal neighbour of centre: 5*100 - (100+200+100+100) = 0
      out.pixel(2, 1).red() shouldBe 0
      // interior pixel with all-100 neighbourhood: 5*100 - 4*100 = 100 (unchanged)
      out.pixel(1, 1).red() shouldBe 100
   }
})
