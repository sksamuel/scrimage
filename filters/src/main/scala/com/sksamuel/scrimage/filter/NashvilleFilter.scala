package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{ Image, Filter, PipelineFilter }
import java.awt.{ Graphics2D, Color }
import thirdparty.romainguy.BlendComposite
import thirdparty.romainguy.BlendComposite.BlendingMode

/** @author Stephen Samuel */
object NashvilleFilter extends PipelineFilter(
  BackgroundBlendFilter,
  HSBFilter(0, -0.2, 0.5),
  GammaFilter(1.2),
  ContrastFilter(1.6),
  VignetteFilter(0.9, 1, 0.6, new Color(255, 140, 0)))

object BackgroundBlendFilter extends Filter {
  def apply(image: Image) {

    val background = image.filled(new Color(51, 0, 0))
    val g3 = image.awt.getGraphics.asInstanceOf[Graphics2D]
    g3.setComposite(BlendComposite.getInstance(BlendingMode.ADD, 1f))
    g3.drawImage(background.awt, 0, 0, null)
    g3.dispose()
  }
}

