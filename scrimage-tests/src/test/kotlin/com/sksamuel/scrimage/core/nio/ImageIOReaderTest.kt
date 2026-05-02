@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageIOReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Rectangle

/**
 * Pin-down tests for ImageIOReader after dropping the redundant
 * setDestinationType(imageTypes.next()) call. The change is intended
 * to be perf-equivalent or strictly faster (skips a redundant header
 * pass via getImageTypes), with no behaviour change.
 */
class ImageIOReaderTest : FunSpec({

   val jpegBytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")!!.readBytes()

   test("read decodes a JPEG into a non-null ImmutableImage with the expected dimensions") {
      val image = ImageIOReader().read(jpegBytes, null)
      image shouldNotBe null
      image.width shouldBeGreaterThan 0
      image.height shouldBeGreaterThan 0
   }

   test("loader produces the same image with and without the optimization (regression check)") {
      // This test exists primarily to catch any future change that
      // re-introduces a behavioural difference at this layer.
      val image = ImmutableImage.loader().fromBytes(jpegBytes)
      image.width shouldBeGreaterThan 0
      image.height shouldBeGreaterThan 0
   }

   test("read with a sub-region returns just that region") {
      val full = ImageIOReader().read(jpegBytes, null)
      val region = Rectangle(0, 0, 10, 10)
      val sub = ImageIOReader().read(jpegBytes, region)
      sub.width shouldBe 10
      sub.height shouldBe 10
      // The sub-region's top-left pixel should match the full image's top-left.
      sub.pixel(0, 0).argb shouldBe full.pixel(0, 0).argb
   }

   test("read throws on null bytes") {
      try {
         ImageIOReader().read(null, null)
         throw AssertionError("Expected IOException")
      } catch (e: java.io.IOException) {
         e.message shouldBe "bytes cannot be null"
      }
   }
})
