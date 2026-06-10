package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression test for zoom(factor < 1) padding the canvas with opaque white.
 *
 * zoom is documented as "scaling this image but without changing the canvas
 * size". When zooming out, the area not covered by the scaled image must be
 * transparent, consistent with the no-color defaults used elsewhere in the
 * library (resizeTo, fit, pad all default to Colors.Transparent).
 *
 * Previously zoom delegated to resizeTo(..., Color.WHITE), which destroyed
 * transparency in PNG workflows.
 */
class ZoomTransparentBackgroundTest : FunSpec({

   test("zoom(0.5) on a transparent PNG keeps corner pixels transparent") {
      // fully opaque red source, round-tripped through PNG to mirror real usage
      val bytes = ImmutableImage.filled(100, 100, Color.RED).bytes(PngWriter.NoCompression)
      val image = ImmutableImage.loader().fromBytes(bytes)

      val zoomed = image.zoom(0.5)

      zoomed.width shouldBe 100
      zoomed.height shouldBe 100

      // corners are outside the scaled-down image and must be fully transparent
      zoomed.pixel(0, 0).alpha() shouldBe 0
      zoomed.pixel(99, 0).alpha() shouldBe 0
      zoomed.pixel(0, 99).alpha() shouldBe 0
      zoomed.pixel(99, 99).alpha() shouldBe 0

      // the centre still contains the scaled image, opaque red
      zoomed.pixel(50, 50).alpha() shouldBe 255
      zoomed.pixel(50, 50).red() shouldBe 255
   }

   test("zoom(0.5) on an image with existing transparency keeps corners at alpha 0") {
      val image = ImmutableImage.create(80, 80).fill(Color(0, 255, 0, 128))

      val zoomed = image.zoom(0.5)

      zoomed.pixel(0, 0).alpha() shouldBe 0
      zoomed.pixel(79, 79).alpha() shouldBe 0
   }
})
