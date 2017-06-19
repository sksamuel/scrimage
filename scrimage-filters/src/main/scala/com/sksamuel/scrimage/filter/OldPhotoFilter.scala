package com.sksamuel.scrimage.filter

import java.awt.Graphics2D

import com.sksamuel.scrimage.{Filter, Image}
import thirdparty.misc.DaisyFilter
import thirdparty.romainguy.{BlendComposite, BlendingMode}

/** @author Stephen Samuel */
object OldPhotoFilter extends Filter {

  val film = Image.fromResource("/com/sksamuel/scrimage/filter/film1.jpg")

  def apply(image: Image) {

    val daisy = new DaisyFilter()
    val filtered = daisy.filter(image.awt)

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.drawImage(filtered, 0, 0, null)
    g2.dispose()

    g2.setComposite(BlendComposite.getInstance(BlendingMode.INVERSE_COLOR_DODGE, 0.30f))
    g2.drawImage(film.scaleTo(image.width, image.height).awt, 0, 0, null)
    g2.dispose()
  }
}

