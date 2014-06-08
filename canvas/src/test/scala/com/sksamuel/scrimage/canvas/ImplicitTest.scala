package com.sksamuel.scrimage.canvas

import org.scalatest.FlatSpec
import com.sksamuel.scrimage.{X11Colorlist, Image}
import com.sksamuel.scrimage.canvas.Canvas._

/** @author Stephen Samuel */
class ImplicitTest extends FlatSpec {

  "an image" should "be implicitly convertable to a canvas" in {
    val image = Image.empty(100, 100)
    val canvas = image.fillRect(X11Colorlist.Azure3, 0, 0, 10, 10)
  }

  "a canvas" should "be implicitly convertable to an image" in {
    val image = Image.empty(100, 100)
    val canvas: Canvas = new Canvas(image)
    canvas.width // operation on an image
  }
}
