package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.metadata.ImageMetadata
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression test for ImmutableImage.scaleTo silently dropping image
 * metadata for every scale method except FastScale.
 *
 * The previous implementation routed Bicubic / Lanczos3 / BSpline /
 * Bilinear / Progressive through `op(...)` (which preserves metadata)
 * and then immediately re-wrapped via wrapAwt(awt) or wrapAwt(awt, type)
 * — both of those overloads use ImageMetadata.empty, silently throwing
 * the metadata away.
 *
 * Net effect: any user who scaled a JPEG before, say, writing it back
 * out with another writer lost their EXIF data on the way.
 */
class ScaleToMetadataTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/vossen.jpg")

   test("source image has non-empty metadata (sanity check on the fixture)") {
      original.metadata.tags().toList().shouldNotBeEmpty()
      original.metadata shouldNotBe ImageMetadata.empty
   }

   test("scaleTo with Bicubic preserves metadata") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.Bicubic)
      scaled.metadata shouldBe original.metadata
   }

   test("scaleTo with Lanczos3 preserves metadata") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.Lanczos3)
      scaled.metadata shouldBe original.metadata
   }

   test("scaleTo with BSpline preserves metadata") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.BSpline)
      scaled.metadata shouldBe original.metadata
   }

   test("scaleTo with Bilinear preserves metadata") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.Bilinear)
      scaled.metadata shouldBe original.metadata
   }

   test("scaleTo with Progressive (downscale) preserves metadata") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.Progressive)
      scaled.metadata shouldBe original.metadata
   }

   test("scaleTo with FastScale preserves metadata (already worked)") {
      val scaled = original.scaleTo(100, 75, ScaleMethod.FastScale)
      scaled.metadata shouldBe original.metadata
   }
})
