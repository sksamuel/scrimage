package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Filter, Image, Pixel}
import thirdparty.jhlabs.image.PixelUtils

/**
  * Works in a similar way to ThresholdFilter, but only blacks those pixels that are below given threshold.
  * @param thresholdPercentage
  */
case class BlackThresholdFilter(thresholdPercentage: Double) extends Filter {

  require(thresholdPercentage >= 0.0 && thresholdPercentage <= 100.0, "Threshold percentage must be between 0 and 100")

  private val threshold = ((255 * thresholdPercentage) / 100.0).toInt

  override def apply(image: Image): Unit = {
    image.mapInPlace { case (_, _, p) =>
      val brightness = PixelUtils.brightness(p.argb)
      if (brightness < threshold) {
        Pixel(p.argb & 0xff000000)
      } else {
        p
      }
    }
  }
}
