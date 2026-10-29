package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.color.Colors
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

// Padding, translating or resizing the canvas with a transparent background drew the image with
// Graphics2D SrcOver, which goes through a premultiplied blit and loses colour precision at low
// alpha: a pixel 0x0540ff80 came back as 0x0533ff99, and alpha 1 pixels lost their colour
// entirely. Over a fully transparent background SrcOver is exactly the source pixel, so these
// geometric operations must reproduce translucent pixels verbatim.
class PadTranslucentPixelsTest : FunSpec({

   // pixel x has alpha x (1..255) and colour 0x40FF80; pixel 0 is opaque
   fun source(): ImmutableImage {
      val img = ImmutableImage.create(256, 1, BufferedImage.TYPE_INT_ARGB)
      img.awt().setRGB(0, 0, 0xFF123456.toInt())
      for (a in 1..255) img.awt().setRGB(a, 0, (a shl 24) or 0x40FF80)
      return img
   }

   fun row(img: ImmutableImage, x: Int, y: Int) = img.awt().getRGB(x, y, 256, 1, null, 0, 256).map { Integer.toHexString(it) }

   val expected = row(source(), 0, 0)

   test("toSquare") { row(source().toSquare(), 0, 127) shouldBe expected }
   test("padTo") { row(source().padTo(258, 5), 1, 2) shouldBe expected }
   test("pad") { row(source().pad(2, Colors.Transparent.awt()), 2, 2) shouldBe expected }
   test("padWith") { row(source().padWith(3, 1, 0, 0), 3, 1) shouldBe expected }
   test("resizeTo") { row(source().resizeTo(258, 5), 1, 2) shouldBe expected }
   test("translate") { row(source().translate(0, 0), 0, 0) shouldBe expected }
   test("translate clips to the canvas") {
      val out = source().translate(-200, 0)
      out.awt().getRGB(0, 0, 56, 1, null, 0, 56).map { Integer.toHexString(it) } shouldBe expected.drop(200)
      out.awt().getRGB(56, 0) shouldBe 0
   }
   test("fit at the same size") { row(source().fit(256, 3, Colors.Transparent.awt(), ScaleMethod.FastScale, com.sksamuel.scrimage.Position.Center), 0, 1) shouldBe expected }

   test("fully transparent source pixels keep the background") {
      val img = ImmutableImage.create(2, 1, BufferedImage.TYPE_INT_ARGB)
      img.awt().setRGB(0, 0, 0x00FFFFFF) // transparent, with hidden colour
      img.awt().setRGB(1, 0, 0x80102030.toInt())
      val out = img.padTo(4, 1)
      out.awt().getRGB(0, 0, 4, 1, null, 0, 4).map { Integer.toHexString(it) } shouldBe
         listOf("0", "0", "80102030", "0")
   }

   test("an opaque background still composites translucent pixels over it") {
      val img = ImmutableImage.create(1, 1, BufferedImage.TYPE_INT_ARGB)
      img.awt().setRGB(0, 0, 0x80FF0000.toInt())
      val px = img.padWith(1, 0, 0, 0, Color.WHITE).awt().getRGB(1, 0)
      // SrcOver of half-transparent red over white: opaque pink
      (px ushr 24) shouldBe 0xFF
      ((px shr 16) and 0xFF) shouldBe 0xFF
      ((px shr 8) and 0xFF) shouldBe 0x7F
   }
})
