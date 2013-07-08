package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{ Image, Filter }
import thirdparty.misc.DaisyFilter
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel */
object OldPhotoFilter extends Filter {

  val film = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/filter/film1.jpg"))

  def apply(image: Image) {

    val daisy = new DaisyFilter()
    val filtered = daisy.filter(image.awt)
    image._draw(filtered)

    val g2 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.INVERSE_COLOR_DODGE, 0.30f))
    g2.drawImage(film.scaleTo(image.width, image.height).awt, 0, 0, null)
    g2.dispose()
  }
}

