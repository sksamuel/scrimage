package com.sksamuel.scrimage.core.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.Canvas
import com.sksamuel.scrimage.canvas.GraphicsContext
import com.sksamuel.scrimage.canvas.drawables.Line
import com.sksamuel.scrimage.canvas.drawables.Polygon
import com.sksamuel.scrimage.canvas.drawables.Rect
import com.sksamuel.scrimage.canvas.painters.ColorPainter
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.color.X11Colorlist
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.AlphaComposite
import java.awt.Color

class DrawableTest : FunSpec({

   fun assertSameImage(img1: ImmutableImage, img2: ImmutableImage) {
      img1.width shouldBe img2.width
      img1.height shouldBe img2.height
      img1.pixels() shouldBe img2.pixels()
   }

   val g2 = GraphicsContext { g2 ->
      g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC)
      g2.color = Color.black
   }

   val blank = ImmutableImage.filled(300, 200, X11Colorlist.White.awt())

   test("The lines are correctly drawn") {

      val canvas = Canvas(blank).draw(
         Line(10, 5, 20, 25, g2),
         Line(30, 50, 30, 200, g2),
         Line(100, 100, 120, 120, g2)
      )
      val img = canvas.image
      val black = RGBColor(0, 0, 0, 255).toARGBInt()
      img.pixel(10, 5).argb shouldBe black
      img.pixel(20, 25).argb shouldBe black
      img.pixel(30, 100).argb shouldBe black
      img.pixel(110, 110).argb shouldBe black
   }

   test("The colors are correctly put") {
      val canvas = Canvas(blank).draw(
         Line(10, 5, 20, 25, g2),
         Line(30, 50, 30, 200) { it.setPainter(ColorPainter(X11Colorlist.Red)) },
         Line(100, 100, 120, 120, g2)
      )
      val img = canvas.image
      val black = RGBColor(0, 0, 0, 255).toARGBInt()
      val red = RGBColor(255, 0, 0, 255).toARGBInt()
      img.pixel(20, 25).argb shouldBe black
      img.pixel(30, 100).argb shouldBe red
      img.pixel(110, 110).argb shouldBe black
   }

   test("Rectangles and polygons draw the same thing") {
      val canvas1 = Canvas(blank).draw(
         Rect(10, 20, 30, 30, g2),
         Rect(100, 120, 50, 20, g2).toFilled()
      )
      val canvas2 = Canvas(blank).draw(
         Polygon.rectangle(10, 20, 30, 30, g2),
         Polygon.rectangle(100, 120, 50, 20, g2).toFilled()
      )
      val img1 = canvas1.image
      val img2 = canvas2.image
      assertSameImage(img1, img2)
   }

})
