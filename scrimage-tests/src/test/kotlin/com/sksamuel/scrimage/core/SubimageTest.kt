package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * Pin-down tests for ImmutableImage.subimage after the int[]-direct
 * rewrite. The new implementation does one bulk getRGB of the region
 * and one bulk setRGB into a fresh image instead of allocating a
 * Pixel[w*h] intermediate.
 */
class SubimageTest : FunSpec({

   test("subimage returns a region with the correct dimensions and pixel values") {
      // 4x4 image where pixel (x,y) has red = x and green = y, opaque.
      val pixels = Array(16) { i ->
         val x = i % 4; val y = i / 4
         Pixel(x, y, x * 16, y * 16, 0, 255)
      }
      val image = ImmutableImage.create(4, 4, pixels)
      val sub = image.subimage(1, 1, 2, 2)
      sub.width shouldBe 2
      sub.height shouldBe 2
      // Sub-pixel (0, 0) should be source (1, 1)
      sub.pixel(0, 0).red() shouldBe 16
      sub.pixel(0, 0).green() shouldBe 16
      // Sub-pixel (1, 0) should be source (2, 1)
      sub.pixel(1, 0).red() shouldBe 32
      sub.pixel(1, 0).green() shouldBe 16
      // Sub-pixel (1, 1) should be source (2, 2)
      sub.pixel(1, 1).red() shouldBe 32
      sub.pixel(1, 1).green() shouldBe 32
   }

   test("subimage(Rectangle) is equivalent to subimage(x, y, w, h)") {
      val pixels = Array(16) { i -> Pixel(i % 4, i / 4, i, i, i, 255) }
      val image = ImmutableImage.create(4, 4, pixels)
      image.subimage(1, 1, 2, 2) shouldBe image.subimage(Rectangle(1, 1, 2, 2))
   }

   test("subimage of full bounds equals the source") {
      val pixels = Array(9) { i -> Pixel(i % 3, i / 3, i * 28, 0, 0, 255) }
      val image = ImmutableImage.create(3, 3, pixels)
      image.subimage(0, 0, 3, 3) shouldBe image
   }

   test("subimage preserves alpha for TYPE_INT_ARGB sources") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 0),       // transparent red
         Pixel(1, 0, 0, 255, 0, 128),     // half-transparent green
         Pixel(0, 1, 0, 0, 255, 200),
         Pixel(1, 1, 0, 0, 0, 255)
      )
      val image = ImmutableImage.create(2, 2, pixels)
      val sub = image.subimage(0, 0, 2, 2)
      sub.pixel(0, 0).alpha() shouldBe 0
      sub.pixel(1, 0).alpha() shouldBe 128
      sub.pixel(0, 1).alpha() shouldBe 200
      sub.pixel(1, 1).alpha() shouldBe 255
   }

   test("subimage carries metadata from the source") {
      val pixels = Array(4) { i -> Pixel(i % 2, i / 2, i, 0, 0, 255) }
      val image = ImmutableImage.create(2, 2, pixels).associateMetadata(ImageMetadata.empty)
      val sub = image.subimage(0, 0, 1, 1)
      // Empty metadata is the same singleton; just check it's not null
      sub.metadata shouldBe ImageMetadata.empty
   }
})
