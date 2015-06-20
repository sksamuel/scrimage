package com.sksamuel.scrimage.filter

import java.awt.{Color, Graphics2D}

import com.sksamuel.scrimage.{AbstractImage, Filter}
import thirdparty.romainguy.BlendComposite._

object BackgroundBlendFilter extends Filter {
  def apply(image: AbstractImage) {

    val background = image.fill(new Color(51, 0, 0))
    val g3 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g3.setComposite(getInstance(BlendingMode.ADD, 1f))
    g3.drawImage(background.awt, 0, 0, null)
    g3.dispose()
    //  image.updateFromAWT()
  }
}
