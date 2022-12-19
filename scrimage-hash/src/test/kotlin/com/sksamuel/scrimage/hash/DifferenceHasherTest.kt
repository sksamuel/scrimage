package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DifferenceHasherTest : FunSpec({

   test("same image should have equals dhash") {
      val image = ImmutableImageLoader.create().fromResource("/spaceworld.jpg")
      image.dhash() shouldBe image.dhash()
      println(image.dhash())
   }

   test("two distinct images should not have the same dhash") {
      val image1 = ImmutableImageLoader.create().fromResource("/spaceworld.jpg")
      val image2 = ImmutableImageLoader.create().fromResource("/landscape.jpg")
      image1.dhash() shouldNotBe image2.dhash()
      println(image2.dhash())
   }
})
