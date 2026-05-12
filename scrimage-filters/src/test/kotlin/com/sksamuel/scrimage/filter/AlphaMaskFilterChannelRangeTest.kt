package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.awt.Color

class AlphaMaskFilterChannelRangeTest : FunSpec({

   // Regression: AlphaMaskFilter constructor validated channel via
   // `assert (channel >= 0 && channel <= 3)`, which is disabled in
   // production JVMs. A channel outside [0, 3] then silently fell
   // through to apply()'s switch default branch (alpha channel),
   // producing the wrong output silently.

   val mask = ImmutableImage.filled(10, 10, Color.RED)

   test("constructor rejects channel below 0") {
      val ex = shouldThrow<IllegalArgumentException> { AlphaMaskFilter(mask, -1) }
      ex.message!!.shouldContain("channel")
      ex.message!!.shouldContain("-1")
   }

   test("constructor rejects channel above 3") {
      val ex = shouldThrow<IllegalArgumentException> { AlphaMaskFilter(mask, 4) }
      ex.message!!.shouldContain("channel")
      ex.message!!.shouldContain("4")
   }

   test("constructor accepts channel 0 (alpha)") {
      AlphaMaskFilter(mask, 0)
   }

   test("constructor accepts channel 3 (blue)") {
      AlphaMaskFilter(mask, 3)
   }
})
