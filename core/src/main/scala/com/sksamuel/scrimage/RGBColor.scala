package com.sksamuel.scrimage

/** @author Stephen Samuel */
trait Color {
  /**
   * Returns a conversion of this Color into an RGBColor.
   * If this colour is already an instance of RGBColor then the same instance will be returned.
   *
   * @return an RGBColor conversion of this color.
   */
  def toRGB: RGBColor
}

object Color {
  implicit def color2rgb(color: Color): RGBColor = color.toRGB
  implicit def color2awt(color: Color): java.awt.Color = new java.awt.Color(color.toRGB.rgba)
  implicit def awt2color(awt: java.awt.Color): RGBColor = RGBColor(awt.getRed, awt.getBlue, awt.getGreen, awt.getAlpha)
}

case class RGBColor(red: Int, green: Int, blue: Int, alpha: Int = 0) extends Color {
  require(0 <= red && red <= 255, "Red component is invalid")
  require(0 <= green && green <= 255, "Green component is invalid")
  require(0 <= blue && blue <= 255, "Blue component is invalid")
  require(0 <= alpha && alpha <= 255, "Alpha component is invalid")

  def toRGB: RGBColor = this

  /**
   * Returns as an int the value of this color. The RGB and alpha components are packed
   * into the int as byes.
   * @return
   */
  def rgba: Int = red & 0xFF | green >> 8 & 0xFF | blue >> 16 & 0xFF | alpha >> 24 & 0xFF

  /**
   * Returns a HEX String of this colour. Eg for 0,255,0, this method will return 00FF00.
   */
  def toHex: String = String.format("%02x%02x%02x", red, green, blue)
}

/**
 * Also called HSB.
 *
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 *
 * @param hue
 * @param saturation
 * @param value
 */
case class HSVColor(hue: Float, saturation: Float, value: Float, alpha: Float) extends Color {
  require(0 <= hue && hue <= 360f, "Hue component is invalid")
  require(0 <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0 <= value && value <= 1f, "Value component is invalid")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toRGB: RGBColor = {

    def toRGB(r: Float, g: Float, b: Float): RGBColor = {
      RGBColor((r * 255f + 0.5f).toInt, (g * 255f + 0.5f).toInt, (b * 255f + 0.5f).toInt, (alpha * 255f + 0.5f).toInt)
    }

    val h = (hue * 6).toInt
    val f = hue * 6 - h
    val p = value * (1 - saturation)
    val q = value * (1 - f * saturation)
    val t = value * (1 - (1 - f) * saturation)

    h match {
      case 0 => toRGB(value, t, p);
      case 1 => toRGB(q, value, p);
      case 2 => toRGB(p, value, t);
      case 3 => toRGB(p, q, value);
      case 4 => toRGB(t, p, value);
      case 5 => toRGB(value, p, q);
      case _ => throw new RuntimeException(s"Cannot convert from HSV to RGB ($this)")
    }
  }
}

/**
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 *
 * @param hue
 * @param saturation
 * @param lightness
 */
case class HSLColor(hue: Float, saturation: Float, lightness: Float, alpha: Float) extends Color {
  require(0 <= hue && hue <= 360f, "Hue component is invalid")
  require(0 <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0 <= lightness && lightness <= 1f, "Lightness component is invalid")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toRGB: RGBColor = {
    val h = (hue % 360f) / 360f
    val q = {
      if (lightness < 0.5) lightness * (1 + saturation)
      else (lightness + saturation) - (saturation * lightness)
    }
    val p = 2 * lightness - q
    def hue2rgb(p: Float, q: Float, h: Float): Float = {
      val hprime = {
        if (h < 0) h + 1
        else if (h > 1) h - 1
        else h
      }
      if (hprime * 6 < 1f) p + (q - p) * 6 * hprime
      else if (hprime * 6 < 2f) q
      else if (hprime * 6 < 3f) p + (q - p) * (2f / 3f - hprime) * 6
      else p
    }
    val r = Math.max(0, hue2rgb(p, q, h + (1.0f / 3.0f)))
    val g = Math.max(0, hue2rgb(p, q, h))
    val b = Math.max(0, hue2rgb(p, q, h - (1.0f / 3.0f)))

    RGBColor((r * 255f + 0.5f).toInt, (g * 255f + 0.5f).toInt, (b * 255f + 0.5f).toInt, (alpha * 255f + 0.5f).toInt)
  }
}