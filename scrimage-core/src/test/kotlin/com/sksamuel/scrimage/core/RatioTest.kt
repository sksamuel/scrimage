@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

class RatioTest : FunSpec({

   test("ratio happy path") {
      val awt1 = BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
      0.5 shouldBe ImmutableImage.fromAwt(awt1).ratio()

      val awt2 = BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB)
      2 shouldBe ImmutableImage.fromAwt(awt2).ratio()

      val awt3 = BufferedImage(333, 333, BufferedImage.TYPE_INT_ARGB)
      1 shouldBe ImmutableImage.fromAwt(awt3).ratio()

      val awt4 = BufferedImage(333, 111, BufferedImage.TYPE_INT_ARGB)
      3.0 shouldBe ImmutableImage.fromAwt(awt4).ratio()

      val awt5 = BufferedImage(111, 333, BufferedImage.TYPE_INT_ARGB)
      1 / 3.0 shouldBe ImmutableImage.fromAwt(awt5).ratio()
   }
})
