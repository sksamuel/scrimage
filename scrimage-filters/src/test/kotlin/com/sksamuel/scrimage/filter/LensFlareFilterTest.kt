package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// Regression coverage for LensFlareFilter, backed by the jhlabs FlareFilter.
// FlareFilter shapes its rays with Noise.noise1, and the static Noise tables are
// seeded from an unseeded Random at class load, so output differs between JVM
// runs and cannot be pinned to a golden image. Output is deterministic within a
// JVM though: applying the same configuration twice must be pixel-identical.
class LensFlareFilterTest : FunSpec({

   val original = ImmutableImage.fromResource("/bird_small.png")

   test("LensFlareFilter preserves dimensions") {
      val out = original.filter(LensFlareFilter())
      out.width shouldBe original.width
      out.height shouldBe original.height
   }

   test("LensFlareFilter modifies the image") {
      original.filter(LensFlareFilter()) shouldNotBe original
   }

   test("LensFlareFilter output is repeatable within the same JVM") {
      val a = original.filter(LensFlareFilter())
      val b = original.filter(LensFlareFilter())
      a shouldBe b
   }
})
