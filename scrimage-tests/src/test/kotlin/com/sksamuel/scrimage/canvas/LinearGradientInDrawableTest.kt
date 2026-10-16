package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.drawables.FilledRect
import com.sksamuel.scrimage.canvas.painters.LinearGradient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression test: LinearGradient.horizontal()/vertical() encode "span the whole image"
 * with sentinel coordinates (Integer.MIN_VALUE .. Integer.MAX_VALUE). Only
 * ImmutableImage.fill(Painter) resolved those sentinels to the real image size; using
 * the same painter through a Canvas drawable's GraphicsContext passed the sentinel
 * GradientPaint straight to AWT, which produced a solid mid-colour (127,0,127) across
 * the whole image instead of a gradient.
 */
class LinearGradientInDrawableTest : FunSpec({

   test("horizontal gradient set via GraphicsContext spans the image left to right") {
      val image = ImmutableImage.create(100, 10, BufferedImage.TYPE_INT_ARGB)
         .toCanvas()
         .draw(FilledRect(0, 0, 100, 10) { g2 -> g2.setPainter(LinearGradient.horizontal(Color.RED, Color.BLUE)) })
         .image
      val left = image.pixel(0, 5)
      val right = image.pixel(99, 5)
      left.red() shouldBeGreaterThan right.red()
      right.blue() shouldBeGreaterThan left.blue()
      left.red() shouldBe 255
      left.blue() shouldBe 0
   }

   test("vertical gradient set via GraphicsContext spans the image top to bottom") {
      val image = ImmutableImage.create(10, 100, BufferedImage.TYPE_INT_ARGB)
         .toCanvas()
         .draw(FilledRect(0, 0, 10, 100) { g2 -> g2.setPainter(LinearGradient.vertical(Color.RED, Color.BLUE)) })
         .image
      val top = image.pixel(5, 0)
      val bottom = image.pixel(5, 99)
      top.red() shouldBeGreaterThan bottom.red()
      bottom.blue() shouldBeGreaterThan top.blue()
      top.red() shouldBe 255
      top.blue() shouldBe 0
   }

   test("drawInPlace resolves the gradient too") {
      val image = ImmutableImage.create(100, 10, BufferedImage.TYPE_INT_ARGB)
      image.toCanvas().drawInPlace(FilledRect(0, 0, 100, 10) { g2 -> g2.setPainter(LinearGradient.horizontal(Color.RED, Color.BLUE)) })
      image.pixel(0, 5).red() shouldBeGreaterThan image.pixel(99, 5).red()
      image.pixel(99, 5).blue() shouldBeGreaterThan image.pixel(0, 5).blue()
   }

   test("explicit-coordinate LinearGradient is passed through unchanged") {
      // a gradient with real coordinates must not be rewritten by the resolution step
      val image = ImmutableImage.create(100, 10, BufferedImage.TYPE_INT_ARGB)
         .toCanvas()
         .draw(FilledRect(0, 0, 100, 10) { g2 -> g2.setPainter(LinearGradient(0, 0, Color.RED, 50, 0, Color.BLUE)) })
         .image
      // GradientPaint is non-cyclic: everything past x=50 is solid blue
      image.pixel(60, 5).blue() shouldBe 255
      image.pixel(99, 5).blue() shouldBe 255
      image.pixel(0, 5).red() shouldBe 255
   }

   test("fill(Painter) output is identical to drawing the same gradient through a Canvas") {
      val viaFill = ImmutableImage.create(100, 10, BufferedImage.TYPE_INT_ARGB)
         .fill(LinearGradient.horizontal(Color.RED, Color.BLUE))
      val viaCanvas = ImmutableImage.create(100, 10, BufferedImage.TYPE_INT_ARGB)
         .toCanvas()
         .draw(FilledRect(0, 0, 100, 10) { g2 -> g2.setPainter(LinearGradient.horizontal(Color.RED, Color.BLUE)) })
         .image
      viaCanvas.argbints().toList() shouldBe viaFill.argbints().toList()
   }
})
