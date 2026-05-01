package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.composite.AlphaComposite
import com.sksamuel.scrimage.metadata.ImageMetadata
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression test for ImmutableImage.copy() silently dropping image
 * metadata, plus the cascade of methods that route through copy()
 * internally.
 *
 * The previous copy() implementation:
 *
 *   public ImmutableImage copy() {
 *      return fromAwt(awt());
 *   }
 *
 * fromAwt(BufferedImage) intentionally uses ImageMetadata.empty (it
 * doesn't know the source's metadata association), so copy() inherited
 * that — every method built on copy() (map, filter, contrast, brightness,
 * removeTransparency, composite, overlay, ...) silently stripped EXIF.
 *
 * This is observed for example with: load JPEG with EXIF → contrast(2)
 * → write JPEG: EXIF gone.
 */
class CopyMetadataTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/vossen.jpg")

   test("source image has non-empty metadata (sanity check on the fixture)") {
      original.metadata.tags().toList().shouldNotBeEmpty()
      original.metadata shouldNotBe ImageMetadata.empty
   }

   test("copy() preserves metadata") {
      original.copy().metadata shouldBe original.metadata
   }

   test("copy(type) preserves metadata") {
      original.copy(BufferedImage.TYPE_INT_ARGB).metadata shouldBe original.metadata
   }

   test("map() preserves metadata (uses copy internally)") {
      original.map { p -> p.toColor().awt() }.metadata shouldBe original.metadata
   }

   test("contrast() preserves metadata (uses copy internally)") {
      original.contrast(1.5).metadata shouldBe original.metadata
   }

   test("brightness() preserves metadata (uses copy internally)") {
      original.brightness(1.2).metadata shouldBe original.metadata
   }

   test("removeTransparency() preserves metadata (uses copy internally)") {
      original.removeTransparency(java.awt.Color.WHITE).metadata shouldBe original.metadata
   }

   test("overlay(image) preserves metadata (uses copy internally)") {
      val overlay = ImmutableImage.create(10, 10, BufferedImage.TYPE_INT_ARGB)
      overlay.fillInPlace(RGBColor(255, 0, 0, 128).awt())
      original.overlay(overlay, 0, 0).metadata shouldBe original.metadata
   }

   test("composite() preserves metadata (uses copy internally)") {
      val applicative = ImmutableImage.create(original.width, original.height, BufferedImage.TYPE_INT_ARGB)
      applicative.fillInPlace(RGBColor(0, 0, 255, 64).awt())
      original.composite(AlphaComposite(0.5), applicative).metadata shouldBe original.metadata
   }
})
