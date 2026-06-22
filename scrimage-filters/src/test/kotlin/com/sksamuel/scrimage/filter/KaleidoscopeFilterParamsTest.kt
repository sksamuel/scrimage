package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Verifies the angle/centre/radius constructor parameters are passed through to
 * the jhlabs KaleidoscopeFilter (each changes the transform, so the output differs).
 */
class KaleidoscopeFilterParamsTest : FunSpec({

   // a non-symmetric gradient so the kaleidoscope transform is observable
   val src = ImmutableImage.create(64, 64).map { p -> Color((p.x() * 4) % 256, (p.y() * 4) % 256, 128) }

   test("angle is passed through") {
      val a = src.filter(KaleidoscopeFilter(3, 0f, 0.5f, 0.5f, 0f))
      val b = src.filter(KaleidoscopeFilter(3, 1.2f, 0.5f, 0.5f, 0f))
      a shouldNotBe b
   }

   test("centre is passed through") {
      val a = src.filter(KaleidoscopeFilter(3, 0f, 0.5f, 0.5f, 0f))
      val b = src.filter(KaleidoscopeFilter(3, 0f, 0.3f, 0.7f, 0f))
      a shouldNotBe b
   }

   test("radius is passed through") {
      val a = src.filter(KaleidoscopeFilter(3, 0f, 0.5f, 0.5f, 0f))
      val b = src.filter(KaleidoscopeFilter(3, 0f, 0.5f, 0.5f, 15f))
      a shouldNotBe b
   }

   test("the sides-only constructor still applies") {
      src.filter(KaleidoscopeFilter(4)).width shouldBe 64
   }
})
