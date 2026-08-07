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

   // Regression: the bounding-box was sized via Math.floor which truncated
   // up to ~1 px from each axis, clipping the rotated image at its edges.
   // Use Math.ceil so the canvas is just big enough to contain the rotation.
   //
   // For a 100x100 image rotated by 45° the real bounding box is 100*sqrt(2)
   // ≈ 141.42 — must be 142 (ceil), not 141 (floor).
   test("rotateByRadians produces a bounding box big enough to contain the rotated image") {
      val image = ImmutableImageLoader.create().fromResource("/bird_small.png")
      val rotated = image.rotate(Degrees(45))
      // ceil of expected real-valued bounding box
      val cos45 = Math.abs(Math.cos(Math.PI / 4))
      val sin45 = Math.abs(Math.sin(Math.PI / 4))
      val expectedW = Math.ceil(image.width * cos45 + image.height * sin45).toInt()
      val expectedH = Math.ceil(image.height * cos45 + image.width * sin45).toInt()
      rotated.width shouldBe expectedW
      rotated.height shouldBe expectedH
   }

   test("rotateByRadians does not change dimensions for axis-aligned 90° rotation") {
      // For 90° / 180° / 270° the bounding box is exact; ceil = floor.
      val rotated = small.rotateLeft()
      rotated.width shouldBe small.height
      rotated.height shouldBe small.width
   }
})
