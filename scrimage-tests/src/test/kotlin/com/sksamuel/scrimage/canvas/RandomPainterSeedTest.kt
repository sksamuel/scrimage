package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.drawables.FilledRect
import com.sksamuel.scrimage.canvas.painters.RandomPainter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression: RandomPainter allocated a `new Random()` inside
 * `getRaster()` — called once per device tile of the draw. The RNG
 * was reseeded with the current time *per tile*, so:
 *
 *  - Output was not reproducible across runs.
 *  - Two adjacent tiles spawning in the same millisecond shared a
 *    seed, producing correlated patterns (visible seams).
 *
 * The fix allocates the RNG once per `paint()` invocation (i.e. once
 * per draw) and adds an optional seed for full determinism.
 */
class RandomPainterSeedTest : FunSpec({

   fun drawn(painter: RandomPainter): ImmutableImage =
      Canvas(ImmutableImage.create(40, 40)).draw(FilledRect(0, 0, 40, 40, GraphicsContext { it.setPaint(painter.paint()) })).image

   test("two RandomPainters with the same seed produce identical images") {
      val a = drawn(RandomPainter(42L))
      val b = drawn(RandomPainter(42L))
      a shouldBe b
   }

   test("two RandomPainters with different seeds produce different images") {
      val a = drawn(RandomPainter(1L))
      val b = drawn(RandomPainter(2L))
      a shouldNotBe b
   }

   test("unseeded RandomPainter still produces (probabilistically) different output across calls") {
      val a = drawn(RandomPainter())
      val b = drawn(RandomPainter())
      // 40*40 = 1600 pixels of independent random colour — the chance
      // of two unseeded draws colliding is negligible.
      a shouldNotBe b
   }
})
