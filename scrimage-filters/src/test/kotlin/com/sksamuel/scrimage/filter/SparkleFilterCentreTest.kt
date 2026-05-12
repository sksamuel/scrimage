package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression: the (x, y) constructor parameters on SparkleFilter were
 * silently discarded. The wrapper used to call
 * `op.setDimensions(x, y)` to communicate the sparkle centre, but the
 * jhlabs PointFilter base immediately calls
 * `setDimensions(srcWidth, srcHeight)` from filter() before iterating
 * pixels — which sets `centreX = width/2; centreY = height/2`, blowing
 * the wrapper's values away. Net effect: every SparkleFilter sparkled
 * at the image centre regardless of the (x, y) supplied at construction.
 *
 * The fix overrides setDimensions in an anonymous jhlabs subclass to
 * re-apply the user-supplied centre after super resets it.
 */
class SparkleFilterCentreTest : FunSpec({

   // Solid black 101x101 canvas — every pixel post-filter is either
   // black (untouched) or some shade brighter (sparkle ray).
   fun blackCanvas(): ImmutableImage =
      ImmutableImage.filled(101, 101, java.awt.Color.BLACK)

   fun luma(image: ImmutableImage, x: Int, y: Int): Int {
      val p = image.pixel(x, y)
      return p.red() + p.green() + p.blue()
   }

   test("explicit (x, y) is honoured — the pixel at the supplied centre is fully sparkled") {
      val img = blackCanvas().filter(SparkleFilter(20, 80, 50, 25, 50))
      // At the sparkle centre, jhlabs' filterRGB computes a mix factor
      // saturating to 1 (distance ~ 0), so the pixel becomes the
      // sparkle colour (opaque white).
      luma(img, 20, 80) shouldBe (255 * 3)
      // Pre-fix the sparkle landed at the image centre, so (20, 80)
      // was untouched (luma 0). Post-fix it is fully sparkled (765).
   }

   test("default no-arg SparkleFilter still sparkles at the image centre") {
      // Sanity-check: (0, 0) sentinel preserves the legacy "centre on
      // the image" default so existing callers don't regress.
      val img = blackCanvas().filter(SparkleFilter())
      luma(img, 50, 50) shouldBeGreaterThan 0
   }

   test("two SparkleFilters at different (x, y) produce different images") {
      val a = blackCanvas().filter(SparkleFilter(10, 10, 50, 25, 50))
      val b = blackCanvas().filter(SparkleFilter(90, 90, 50, 25, 50))
      // Pre-fix both were identical (sparkle always at centre).
      a shouldNotBe b
   }
})
