package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

/**
 * Regression tests for geometric operations on palette-based (IndexColorModel) images.
 *
 * subimage (and trim/take/drop/autocrop which delegate to it), blank() (used by
 * fill/translate/underlay), rotate and the FastScale nearest-neighbour scaler all
 * created their target with `new BufferedImage(w, h, TYPE_BYTE_INDEXED)`, which gets
 * Java's *default* 6x6x6 colour-cube palette rather than the source image's palette.
 * The subsequent setRGB then snapped every pixel to the nearest default-palette entry,
 * so a plain crop or 90 degree rotation of a GIF changed every colour in the image.
 *
 * The target must instead reuse the source's own IndexColorModel (as copy() already
 * does since #837). Where every output pixel is a source pixel the result is then
 * exact; background colours (translate/rotate/fill) that are not in the source
 * palette snap to the nearest *source* palette entry.
 */
class PaletteGeometricOpsTest : FunSpec({

   // off-cube colours: none of these are in Java's default TYPE_BYTE_INDEXED palette
   fun offCubePalette(): IndexColorModel {
      val r = byteArrayOf(123.toByte(), 10.toByte(), 200.toByte(), 77.toByte())
      val g = byteArrayOf(45.toByte(), 200.toByte(), 100.toByte(), 77.toByte())
      val b = byteArrayOf(67.toByte(), 30.toByte(), 250.toByte(), 77.toByte())
      return IndexColorModel(8, 4, r, g, b)
   }

   fun indexedFixture(w: Int = 6, h: Int = 4): ImmutableImage {
      val icm = offCubePalette()
      val buf = BufferedImage(icm, icm.createCompatibleWritableRaster(w, h), false, null)
      for (y in 0 until h) for (x in 0 until w) buf.raster.setSample(x, y, 0, (x + y) % 4)
      return ImmutableImage.wrapAwt(buf)
   }

   // a colour that IS in the fixture palette, usable as an exact background
   val paletteGray = Color(77, 77, 77)

   fun ImmutableImage.argbList(): List<Int> = argbints().toList()

   fun ImmutableImage.shouldStayIndexed() {
      awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      (awt().colorModel is IndexColorModel) shouldBe true
   }

   test("subimage on a custom-palette indexed image preserves the colours and the palette") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      val out = img.subimage(1, 1, 3, 2)
      out.shouldStayIndexed()
      out.argbList() shouldBe argb.subimage(1, 1, 3, 2).argbList()
   }

   test("trim / takeLeft / dropTop on a custom-palette indexed image preserve the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      img.trim(1).argbList() shouldBe argb.trim(1).argbList()
      img.takeLeft(3).argbList() shouldBe argb.takeLeft(3).argbList()
      img.dropTop(1).argbList() shouldBe argb.dropTop(1).argbList()
   }

   test("rotateLeft / rotateRight on a custom-palette indexed image preserve the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      img.rotateLeft().shouldStayIndexed()
      img.rotateLeft().argbList() shouldBe argb.rotateLeft().argbList()
      img.rotateRight().argbList() shouldBe argb.rotateRight().argbList()
   }

   test("FastScale upscale on a custom-palette indexed image preserves the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      val out = img.scaleTo(12, 8, ScaleMethod.FastScale)
      out.shouldStayIndexed()
      out.argbList() shouldBe argb.scaleTo(12, 8, ScaleMethod.FastScale).argbList()
   }

   test("FastScale downscale on a custom-palette indexed image preserves the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      img.scaleTo(3, 2, ScaleMethod.FastScale).argbList() shouldBe argb.scaleTo(3, 2, ScaleMethod.FastScale).argbList()
   }

   test("translate with an in-palette background on a custom-palette indexed image preserves the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      val out = img.translate(1, 1, paletteGray)
      out.shouldStayIndexed()
      out.argbList() shouldBe argb.translate(1, 1, paletteGray).argbList()
   }

   test("fit with FastScale and an in-palette background on a custom-palette indexed image preserves the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      val out = img.fit(10, 10, paletteGray, ScaleMethod.FastScale, Position.TopLeft)
      out.argbList() shouldBe argb.fit(10, 10, paletteGray, ScaleMethod.FastScale, Position.TopLeft).argbList()
   }

   test("underlay on a custom-palette indexed image preserves the colours") {
      val img = indexedFixture()
      val argb = img.copy(BufferedImage.TYPE_INT_ARGB)
      val under = ImmutableImage.filled(6, 4, paletteGray)
      img.underlay(under).argbList() shouldBe argb.underlay(under).argbList()
   }

   test("fill with an in-palette colour on a custom-palette indexed image is exact") {
      val img = indexedFixture()
      img.fill(Color(123, 45, 67)).argbList().distinct() shouldBe listOf(Color(123, 45, 67).rgb)
   }

   test("fill with an off-palette colour snaps to the nearest SOURCE palette entry, not the default palette") {
      // nearest of (123,45,67) (10,200,30) (200,100,250) (77,77,77) to black is (77,77,77);
      // the default 6x6x6 palette would have produced exact black
      val img = indexedFixture()
      img.fill(Color.BLACK).argbList().distinct() shouldBe listOf(paletteGray.rgb)
   }

   test("loaded GIF: subimage preserves the colours") {
      val gif = ImmutableImage.loader().fromResource("/github174.gif")
      gif.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED
      val argb = gif.copy(BufferedImage.TYPE_INT_ARGB)
      gif.subimage(10, 10, 50, 50).argbList() shouldBe argb.subimage(10, 10, 50, 50).argbList()
   }

   test("loaded GIF: FastScale preserves the colours") {
      val gif = ImmutableImage.loader().fromResource("/github174.gif")
      val argb = gif.copy(BufferedImage.TYPE_INT_ARGB)
      gif.scaleTo(87, 87, ScaleMethod.FastScale).argbList() shouldBe argb.scaleTo(87, 87, ScaleMethod.FastScale).argbList()
   }

   test("loaded GIF: rotateLeft preserves the colours") {
      val gif = ImmutableImage.loader().fromResource("/github174.gif")
      val argb = gif.copy(BufferedImage.TYPE_INT_ARGB)
      gif.rotateLeft().argbList() shouldBe argb.rotateLeft().argbList()
   }

   test("loaded GIF: translate preserves the colours of the moved region") {
      val gif = ImmutableImage.loader().fromResource("/github174.gif")
      val argb = gif.copy(BufferedImage.TYPE_INT_ARGB)
      val a = gif.translate(5, 5).subimage(5, 5, gif.width - 5, gif.height - 5)
      val b = argb.translate(5, 5).subimage(5, 5, gif.width - 5, gif.height - 5)
      a.argbList() shouldBe b.argbList()
   }
})
