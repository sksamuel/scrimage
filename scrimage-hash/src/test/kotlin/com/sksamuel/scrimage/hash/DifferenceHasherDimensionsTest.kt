package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * Regression test: DifferenceHasher accepted any cols/rows. Negative or zero values
 * were only rejected later, inside dhash(), by the scaling step ("Width (-1) and
 * height (8) cannot be <= 0"), far from the constructor call that was wrong; and
 * cols == 1 was accepted outright and silently returned an empty hash because a
 * dhash compares horizontally adjacent pixels and therefore needs at least two columns.
 */
class DifferenceHasherDimensionsTest : FunSpec({

   val image = ImmutableImage.filled(32, 32, Color.GRAY).map { p ->
      Color(p.x * 8, p.y * 8, 128)
   }

   test("negative cols is rejected with IllegalArgumentException") {
      val e = shouldThrow<IllegalArgumentException> { DifferenceHasher(-1, 8) }
      e.message shouldContain "cols"
   }

   test("negative rows is rejected with IllegalArgumentException") {
      val e = shouldThrow<IllegalArgumentException> { DifferenceHasher(9, -1) }
      e.message shouldContain "rows"
   }

   test("zero rows is rejected with IllegalArgumentException") {
      shouldThrow<IllegalArgumentException> { DifferenceHasher(9, 0) }
   }

   test("a single column is rejected because adjacent pixels cannot be compared") {
      val e = shouldThrow<IllegalArgumentException> { DifferenceHasher(1, 8) }
      e.message shouldContain "cols"
   }

   test("default (9, 8) still yields 64 bits") {
      DifferenceHasher(9, 8).dhash(image).size shouldBe 64
      image.dhash().size shouldBe 64
   }

   test("minimum (2, 1) yields a single bit") {
      DifferenceHasher(2, 1).dhash(image).size shouldBe 1
   }
})
