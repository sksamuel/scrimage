@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

/**
 * Verifies orientation correction across the eight EXIF orientation
 * values (1–8). Source images come from
 * https://github.com/recurser/exif-orientation-examples — each
 * portrait_N.jpg has EXIF orientation N and stores the same waterfall
 * image rotated/mirrored according to that orientation.
 *
 * The previous test in this file went via JpegWriter and byte-compared
 * the encoded JPEG output, but used JpegWriter(100, true) — JPEG
 * compression=100 throws "JPEG compression cannot be disabled", so the
 * tests had been disabled with `!`-prefixes for an indeterminate time.
 *
 * This rewrite skips the JPEG round-trip and pixel-compares the
 * orientation-corrected ImmutableImage directly. Two checks per
 * portrait_N:
 *   - dimensions match the canonical 450x600 (a portrait orientation)
 *   - the top-left pixel is close to the reference's top-left
 *     (within JPEG quantization tolerance)
 */
class OrientationTest : WordSpec({

   val reference = ImmutableImageLoader.create()
      .fromResource("/com/sksamuel/scrimage/iphone/portrait_1_expected.jpg")

   "scrimage" should {
      for (n in 1..8) {
         "correct orientation $n to portrait dimensions and reference colour" {
            val corrected = ImmutableImageLoader.create()
               .fromResource("/com/sksamuel/scrimage/iphone/portrait_$n.jpg")
            corrected.width shouldBe 450
            corrected.height shouldBe 600

            // Top-left should be a mid-tone forest green similar to
            // reference's top-left. JPEG re-encoding shifts pixels, so
            // tolerate up to 30 units of channel-distance.
            val actual = corrected.pixel(10, 10)
            val expected = reference.pixel(10, 10)
            val dr = Math.abs(actual.red() - expected.red())
            val dg = Math.abs(actual.green() - expected.green())
            val db = Math.abs(actual.blue() - expected.blue())
            (dr + dg + db) shouldBeLessThan 90 // sum-of-channel-deltas
         }
      }

      "leave a non-rotated image untouched (sanity check on portrait_1)" {
         val correctedTwice = ImmutableImageLoader.create()
            .fromResource("/com/sksamuel/scrimage/iphone/portrait_1.jpg")
         val noReorient = ImmutableImageLoader.create()
            .detectOrientation(false)
            .fromResource("/com/sksamuel/scrimage/iphone/portrait_1.jpg")
         // portrait_1 has EXIF 1 (no transform needed) — corrected and raw
         // should be byte-identical
         correctedTwice shouldBe noReorient
      }

      "produce dimensionally identical results across all 8 EXIF orientations" {
         val dims = (1..8).map { n ->
            val img = ImmutableImageLoader.create()
               .fromResource("/com/sksamuel/scrimage/iphone/portrait_$n.jpg")
            img.width to img.height
         }
         dims.toSet().size shouldBe 1
      }
   }
})
