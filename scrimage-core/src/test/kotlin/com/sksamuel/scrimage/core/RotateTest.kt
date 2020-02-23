package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RotateTest : FunSpec({

   val small = ImmutableImage.fromResource("/bird_small.png")

   test("when rotating left the width and height are reversed") {
      val rotated = small.rotateLeft()
      259 shouldBe rotated.width
      388 shouldBe rotated.height
      rotated shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_rotated_left.png")
   }

   test("when rotating right the width and height are reversed") {
      val rotated = small.rotateRight()
      259 shouldBe rotated.width
      388 shouldBe rotated.height
      rotated shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_rotated_right.png")
   }

})
