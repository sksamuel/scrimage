package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.canvas.painters.LinearGradient
import com.sksamuel.scrimage.metadata.ImageMetadata
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Regression test for the canvas-resize family of methods silently
 * dropping image metadata.
 *
 * Even after #421 (copy() preserves metadata), the methods that build
 * a fresh canvas via filled() or blank() and overlay onto it — resize,
 * resizeTo, resizeToHeight/Width/Ratio, pad / padTo / padWith / padTop /
 * padBottom / padLeft / padRight, translate, cover, fit, underlay,
 * fill — still threw EXIF away.
 *
 * Each canonical entry point now re-attaches the source's metadata at
 * the end. Convenience overloads benefit transitively.
 */
class CanvasOpsMetadataTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/vossen.jpg")

   test("source image has non-empty metadata (sanity check)") {
      original.metadata.tags().toList().shouldNotBeEmpty()
      original.metadata shouldNotBe ImageMetadata.empty
   }

   test("resize preserves metadata") {
      original.resize(0.5).metadata shouldBe original.metadata
   }

   test("resizeTo preserves metadata") {
      original.resizeTo(100, 100).metadata shouldBe original.metadata
   }

   test("resizeToHeight preserves metadata") {
      original.resizeToHeight(50).metadata shouldBe original.metadata
   }

   test("resizeToWidth preserves metadata") {
      original.resizeToWidth(50).metadata shouldBe original.metadata
   }

   test("resizeToRatio preserves metadata") {
      original.resizeToRatio(0.5).metadata shouldBe original.metadata
   }

   test("padWith preserves metadata") {
      original.padWith(5, 5, 5, 5, Color.RED).metadata shouldBe original.metadata
   }

   test("pad preserves metadata") {
      original.pad(10, Color.RED).metadata shouldBe original.metadata
   }

   test("padTo preserves metadata") {
      original.padTo(original.width + 20, original.height + 20).metadata shouldBe original.metadata
   }

   test("padTop preserves metadata") {
      original.padTop(5, Color.RED).metadata shouldBe original.metadata
   }

   test("padBottom preserves metadata") {
      original.padBottom(5, Color.RED).metadata shouldBe original.metadata
   }

   test("padLeft preserves metadata") {
      original.padLeft(5).metadata shouldBe original.metadata
   }

   test("padRight preserves metadata") {
      original.padRight(5).metadata shouldBe original.metadata
   }

   test("translate preserves metadata") {
      original.translate(10, 10).metadata shouldBe original.metadata
   }

   test("cover preserves metadata") {
      original.cover(200, 200).metadata shouldBe original.metadata
   }

   test("fit preserves metadata") {
      original.fit(200, 200).metadata shouldBe original.metadata
   }

   test("fill(Color) preserves metadata") {
      original.fill(Color.RED).metadata shouldBe original.metadata
   }

   test("fill(Painter) preserves metadata") {
      original.fill(LinearGradient.horizontal(Color.RED, Color.BLUE)).metadata shouldBe original.metadata
   }

   test("underlay preserves metadata") {
      val under = ImmutableImage.create(original.width, original.height)
      original.underlay(under).metadata shouldBe original.metadata
   }

   test("zoom preserves metadata") {
      original.zoom(1.5).metadata shouldBe original.metadata
   }
})
