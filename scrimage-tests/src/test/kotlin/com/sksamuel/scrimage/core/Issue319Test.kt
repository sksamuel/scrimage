package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

class Issue319Test : FunSpec() {
   init {
      test("cover 505x505 ushort gray image to 512x512 should not throw ArrayIndexOutOfBoundsException") {
         val bufferedImage = BufferedImage(505, 505, BufferedImage.TYPE_USHORT_GRAY)
         val image = ImmutableImage.wrapAwt(bufferedImage)
         val covered = image.cover(512, 512)
         covered.width shouldBe 512
         covered.height shouldBe 512
      }
   }
}
