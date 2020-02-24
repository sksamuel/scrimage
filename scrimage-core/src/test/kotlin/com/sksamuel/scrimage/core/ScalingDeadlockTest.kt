package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class ScalingDeadlockTest : FunSpec({

   val image = ImmutableImage.fromStream(javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))

   test("image scale should not deadlock on multiple concurrent scales") {
      val images = mutableListOf<ImmutableImage>()
      val latch = CountDownLatch(50)
      repeat(50) {
         thread {
            images.add(image.scaleTo(200, 200))
            latch.countDown()
         }
      }
      latch.await()
      images.size shouldBe 50
      images.forAll { it.width shouldBe 200 }
   }

})
