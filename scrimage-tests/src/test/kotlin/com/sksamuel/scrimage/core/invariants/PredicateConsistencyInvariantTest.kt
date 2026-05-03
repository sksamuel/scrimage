package com.sksamuel.scrimage.core.invariants

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.util.function.Predicate

/**
 * Property-style tests for cross-consistency between AwtImage's predicate
 * methods. Each takes a different fast path through the pixel buffer,
 * so divergence (off-by-one in a short-circuit, wrong default for an
 * empty buffer, predicate-result type mix-up) would surface here.
 *
 *   count(p -> true)    ==  width * height
 *   count(p -> false)   ==  0
 *   forAll(p)           <=>  count(p)  ==  width * height
 *   exists(p)           <=>  count(p)  >  0
 *   forAll(p)           <=>  !exists(!p)
 *   exists(p)           <=>  !forAll(!p)
 *   forEach visits every pixel exactly once
 */
class PredicateConsistencyInvariantTest : FunSpec({

   fun seed(width: Int, height: Int, type: Int): ImmutableImage {
      val buf = BufferedImage(width, height, type)
      for (y in 0 until height) for (x in 0 until width) {
         val r = (x * 11) and 0xFF
         val g = (y * 13) and 0xFF
         val b = ((x + y) * 7) and 0xFF
         buf.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
      }
      return ImmutableImage.wrapAwt(buf)
   }

   val cases = listOf(
      Triple(1, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(8, 1, BufferedImage.TYPE_INT_ARGB),
      Triple(1, 8, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_ARGB),
      Triple(7, 13, BufferedImage.TYPE_INT_RGB),
      Triple(7, 13, BufferedImage.TYPE_4BYTE_ABGR),
      Triple(32, 24, BufferedImage.TYPE_INT_ARGB),
   )

   // A handful of predicates that partition the seeded pixels into
   // varying populations: never matches, always matches, half matches,
   // a thin band, and a single pixel.
   val predicates: List<Predicate<Pixel>> = listOf(
      Predicate { false },
      Predicate { true },
      Predicate { p -> p.x % 2 == 0 },
      Predicate { p -> p.y == 0 },
      Predicate { p -> p.x == 0 && p.y == 0 },
   )

   test("count(p -> true) equals width * height") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         img.count { true } shouldBe (w.toLong() * h)
      }
   }

   test("count(p -> false) equals zero") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         img.count { false } shouldBe 0L
      }
   }

   test("forAll(p) iff count(p) == width * height") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val total = w.toLong() * h
         for (p in predicates) {
            val all = img.forAll(p)
            val cnt = img.count(p)
            (all == (cnt == total)) shouldBe true
         }
      }
   }

   test("exists(p) iff count(p) > 0") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (p in predicates) {
            val any = img.exists(p)
            val cnt = img.count(p)
            (any == (cnt > 0)) shouldBe true
         }
      }
   }

   test("forAll(p) iff !exists(!p)") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (p in predicates) {
            val all = img.forAll(p)
            val notAny = !img.exists(p.negate())
            (all == notAny) shouldBe true
         }
      }
   }

   test("exists(p) iff !forAll(!p)") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         for (p in predicates) {
            val any = img.exists(p)
            val notAll = !img.forAll(p.negate())
            (any == notAll) shouldBe true
         }
      }
   }

   test("forEach visits every pixel exactly once") {
      for ((w, h, t) in cases) {
         val img = seed(w, h, t)
         val visited = HashSet<Pair<Int, Int>>()
         img.forEach { pixel -> visited.add(pixel.x to pixel.y) }
         visited.size shouldBe w * h
         // Every coordinate should be present.
         for (y in 0 until h) for (x in 0 until w) {
            visited.contains(x to y).shouldBeTrue()
         }
      }
   }
})
