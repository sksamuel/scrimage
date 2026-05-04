package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.util.Random

class SwimFilterRandomTest : FunSpec({

   // Regression for SwimFilter(Random) constructor previously delegating to
   // `this(new Random(), 6f, 2f)` — discarding the seeded Random the caller
   // passed in. That broke deterministic / reproducible tests because every
   // invocation got a fresh non-deterministic Random regardless of what was
   // passed in.
   //
   // With a properly threaded-through seed, two SwimFilter instances built
   // from two Randoms with the same seed must produce byte-identical output
   // when applied to the same source image.
   test("SwimFilter(Random) honours the seed it's given") {
      val source = ImmutableImage.filled(64, 32, Color.RED)

      val a = source.filter(SwimFilter(Random(42L)))
      val b = source.filter(SwimFilter(Random(42L)))

      // Same seed → same output. Previously the SwimFilter(Random) constructor
      // ignored the parameter, so two invocations diverged.
      a.bytes(com.sksamuel.scrimage.nio.PngWriter.NoCompression)
         .contentEquals(b.bytes(com.sksamuel.scrimage.nio.PngWriter.NoCompression)) shouldBe true
   }
})
