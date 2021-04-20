package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ImageCreatorsTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

   test("when creating a blank copy then the dimensions are the same as the original") {
      val copy = image.blank()
      1944 shouldBe copy.width
      1296 shouldBe copy.height
   }

   test("when create a filled image then the dimensions are as specified") {
      val i = ImmutableImage.filled(595, 911, java.awt.Color.BLACK)
      595 shouldBe i.width
      911 shouldBe i.height
   }

   test("when creating a empty image then the dimensions are as specified") {
      val i = ImmutableImage.create(80, 90)
      80 shouldBe i.width
      90 shouldBe i.height
   }

})
