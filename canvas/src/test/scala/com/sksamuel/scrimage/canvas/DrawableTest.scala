package com.sksamuel.scrimage.canvas


import org.scalatest.FunSuite
import com.sksamuel.scrimage.{X11Colorlist, Image}

class DrawableTest extends FunSuite {
  val blank = Image.filled(200, 100, X11Colorlist.White)

  test("The lines are correctly drawn") {
    val image1 = new Canvas(blank).drawLine(10, 5, 20, 25)
        .drawLine(30, 50, 200, 200)
        .drawLine(100, 100, 120, 130)
    val image2 = new Canvas(blank)
    image2.draw(DLine(10, 5, 20, 25),
                DLine(30, 50, 200, 200),
                DLine(100, 100, 120, 130))
    assert(image1 == image2)
  }
}
