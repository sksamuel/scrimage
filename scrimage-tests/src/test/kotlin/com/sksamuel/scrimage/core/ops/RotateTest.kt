package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.angles.Degrees
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class RotateTest : FunSpec({

   val small = ImmutableImageLoader.create().fromResource("/bird_small.png")

   test("when rotating left the width and height are reversed") {
      val rotated = small.rotateLeft()
      259 shouldBe rotated.width
      388 shouldBe rotated.height
      rotated shouldBe ImmutableImageLoader.create().fromResource("/com/sksamuel/scrimage/rotate/bird_rotated_left.png")
   }

   test("when rotating right the width and height are reversed") {
      val rotated = small.rotateRight()
      259 shouldBe rotated.width
      388 shouldBe rotated.height
      rotated shouldBe ImmutableImageLoader.create()
         .fromResource("/com/sksamuel/scrimage/rotate/bird_rotated_right.png")
   }

   test("arbitrary rotation by degrees") {
      for (k in 1..10) {
         val rotated = small.rotate(Degrees(k))
         rotated shouldBe ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/rotate/bird_rotated_${k}deg.png")
      }
   }

   test("arbitrary rotation with background") {
      val rotated = small.rotate(Degrees(2), Color.GREEN)
      rotated shouldBe ImmutableImageLoader.create()
         .fromResource("/com/sksamuel/scrimage/rotate/bird_rotated_with_bg.png")
   }
})
