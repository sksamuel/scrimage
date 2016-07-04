package com.sksamuel.scrimage.canvas

import java.awt.{AlphaComposite, Graphics2D}

import com.sksamuel.scrimage.canvas.drawable.{Line, Polygon, Rect}
import com.sksamuel.scrimage.{Color, Image, Pixel, X11Colorlist}
import org.scalatest.FunSuite

class DrawableTest extends FunSuite {

  def assertSameImage(img1: Image, img2: Image): Unit = {
    assert(img1.width === img2.width)
    assert(img1.height === img2.height)
    for ( x <- 0 until img1.width; y <- 0 until img2.height ) {
      assert(img1.pixel(x, y) === img2.pixel(x, y))
    }
  }

  val g2: Graphics2D => Unit = { g2 =>
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC))
    g2.setColor(Color.Black)
  }

  val blank = Image.filled(300, 200, X11Colorlist.White)

  test("The lines are correctly drawn") {

    val canvas = new Canvas(blank).draw(
      Line(10, 5, 20, 25, g2),
      Line(30, 50, 30, 200, g2),
      Line(100, 100, 120, 120, g2)
    )
    val img = canvas.image
    val black = Pixel(X11Colorlist.Black.toInt)
    assert(img.pixel(10, 5) === black)
    assert(img.pixel(20, 25) === black)
    assert(img.pixel(30, 100) === black)
    assert(img.pixel(110, 110) === black)
  }

  test("The colors are correctly put") {
    val canvas = new Canvas(blank).draw(
      Line(10, 5, 20, 25, g2),
      Line(30, 50, 30, 200, Context.painter(X11Colorlist.Red)),
      Line(100, 100, 120, 120, g2)
    )
    val img = canvas.image
    val black = X11Colorlist.Black.toInt
    val red = X11Colorlist.Red.toInt
    assert(img.pixel(20, 25) === Pixel(black))
    assert(img.pixel(30, 100) === Pixel(red))
    assert(img.pixel(110, 110) === Pixel(black))
  }

  test("Rectangles and polygons draw the same thing") {
    val canvas1 = new Canvas(blank).draw(
      Rect(10, 20, 30, 30),
      Rect(100, 120, 50, 20).fill
    )
    val canvas2 = new Canvas(blank).draw(
      Polygon.rectangle(10, 20, 30, 30),
      Polygon.rectangle(100, 120, 50, 20).fill
    )
    val img1 = canvas1.image
    val img2 = canvas2.image
    assertSameImage(img1, img2)
  }
}
