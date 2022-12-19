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

   test("slightly rotated images should have similar dhashes") {
      val image1 = ImmutableImageLoader.create().fromResource("/spaceworld.jpg")
      val image2 = ImmutableImageLoader.create().fromResource("/spaceworld_rot.jpg")
      println(hamming(image1.dhash(), image2.dhash()))
   }

   test("slightly rotated and cropped images should have similar dhashes") {
      val image1 = ImmutableImageLoader.create().fromResource("/spaceworld.jpg")
      val image2 = ImmutableImageLoader.create().fromResource("/spaceworld_rot_crop.jpg")
      println(hamming(image1.dhash(), image2.dhash()))
   }
})

fun hamming(a: List<Int>, b: List<Int>): Int {
   return a.zip(b).count { it.first != it.second }
}
