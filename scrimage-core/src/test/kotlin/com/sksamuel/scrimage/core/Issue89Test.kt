package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Issue89Test : WordSpec({

   "Loading image" should {
      "not randomly rotate"  {
         repeat(10) {
            val image = ImmutableImage.fromResource("/issue89.jpg")
            image.width shouldBe 319
            image.height shouldBe 397
            val bytes = image.bytes(JpegWriter())
            val reread = ImmutableImage.parse(bytes)
            reread.width shouldBe 319
            reread.height shouldBe 397
         }
      }
   }

})
