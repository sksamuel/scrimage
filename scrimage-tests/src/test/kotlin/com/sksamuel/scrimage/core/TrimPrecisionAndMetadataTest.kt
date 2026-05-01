package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression test for two bugs in ImmutableImage.trim and its
 * trimLeft/trimRight/trimTop/trimBottom convenience methods.
 *
 * Pre-fix implementation:
 *
 *   public ImmutableImage trim(int left, int top, int right, int bottom) {
 *      return create(...).overlay(this, -left, -top);
 *   }
 *
 * Two distinct problems with that path:
 *
 *  1. **Channel precision loss for alpha != 255.** create(...).overlay(...)
 *     goes through Graphics2D.drawImage, which composites via SrcOver and
 *     premultiplies alpha — losing ±1 per colour channel when alpha doesn't
 *     divide 255 cleanly. Same root cause as #414 (clone(int)) and #416
 *     (toNewBufferedImage(int)).
 *
 *  2. **Metadata loss.** create(...) returns a fresh image with empty
 *     metadata; overlay(this, ...) preserves the receiver's metadata
 *     (after #421), which means the trimmed result inherits the empty
 *     metadata from the freshly-created canvas, not the EXIF of the
 *     source.
 *
 * The fix routes trim through subimage(...), which is bit-exact and
 * already preserves metadata.
 */
class TrimPrecisionAndMetadataTest : FunSpec({

   test("trim preserves channel values exactly when alpha != 255") {
      // 3x3 image with one alpha=128 pixel that should survive a 1-pixel
      // trim around the edges. SrcOver premultiplication would shift
      // (r=100, g=150, b=200) by ±1 — easy regression to catch.
      val pixels = Array(9) { i ->
         val x = i % 3; val y = i / 3
         if (x == 1 && y == 1) Pixel(x, y, 100, 150, 200, 128)
         else Pixel(x, y, 0, 0, 0, 255)
      }
      val image = ImmutableImage.create(3, 3, pixels)
      val trimmed = image.trim(1, 1, 1, 1)
      trimmed.width shouldBe 1
      trimmed.height shouldBe 1
      val p = trimmed.pixel(0, 0)
      p.red() shouldBe 100
      p.green() shouldBe 150
      p.blue() shouldBe 200
      p.alpha() shouldBe 128
   }

   test("trim preserves image metadata") {
      val original = ImmutableImage.loader().fromResource("/vossen.jpg")
      original.metadata shouldNotBe ImageMetadata.empty
      original.trim(10, 10, 10, 10).metadata shouldBe original.metadata
   }

   test("trimLeft / trimRight / trimTop / trimBottom preserve metadata") {
      val original = ImmutableImage.loader().fromResource("/vossen.jpg")
      original.trimLeft(5).metadata shouldBe original.metadata
      original.trimRight(5).metadata shouldBe original.metadata
      original.trimTop(5).metadata shouldBe original.metadata
      original.trimBottom(5).metadata shouldBe original.metadata
   }

   test("trim returns the expected sub-region of pixels") {
      // 4x4 image where pixel (x,y) has red = x*16, green = y*16
      val pixels = Array(16) { i ->
         val x = i % 4; val y = i / 4
         Pixel(x, y, x * 16, y * 16, 0, 255)
      }
      val image = ImmutableImage.create(4, 4, pixels)
      val trimmed = image.trim(1, 1, 1, 1)
      trimmed.width shouldBe 2
      trimmed.height shouldBe 2
      // (0,0) of trimmed = (1,1) of source
      trimmed.pixel(0, 0).red() shouldBe 16
      trimmed.pixel(0, 0).green() shouldBe 16
      // (1,1) of trimmed = (2,2) of source
      trimmed.pixel(1, 1).red() shouldBe 32
      trimmed.pixel(1, 1).green() shouldBe 32
   }
})
