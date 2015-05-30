package com.sksamuel.scrimage

import java.awt.Paint

import scala.language.implicitConversions

/**
 * @author Stephen Samuel
 */
trait Color {

  /**
   * Returns a conversion of this Color into an RGBColor.
   * If this colour is already an instance of RGBColor then the same instance will be returned.
   */
  def toRGB: RGBColor

  /**
   * Returns a conversion of this color into a CYMK color.
   * If this colour is already a CYMK then the same instance will be returned.
   */
  def toCYMK: CMYK = toRGB.toCYMK

  def toHSV: HSVColor = toRGB.toHSV

  def toHSL: HSLColor = toRGB.toHSL

  def toHex: String = toRGB.toHex

  /**
   * Returns an AWT Color representation of this colour.
   * AWT Colors are in the RGB color model.
   */
  def toAWT: java.awt.Color = {
    val rgb = toRGB
    new java.awt.Color(rgb.red, rgb.green, rgb.blue, rgb.alpha)
  }

  private[scrimage] def paint: Paint = new java.awt.Color(this.toRGB.toInt)
}

object Color {

  implicit def int2color(argb: Int): RGBColor = apply(argb)
  implicit def color2rgb(color: Color): RGBColor = color.toRGB
  implicit def color2awt(color: Color): java.awt.Color = new java.awt.Color(color.toRGB.toInt)
  implicit def awt2color(awt: java.awt.Color): RGBColor = RGBColor(awt.getRed, awt.getGreen, awt.getBlue, awt.getAlpha)

  def apply(red: Int, green: Int, blue: Int, alpha: Int = 255): RGBColor = RGBColor(red, green, blue, alpha)
  def apply(argb: Int): RGBColor = {
    val alpha = (argb >> 24) & 0xFF
    val red = (argb >> 16) & 0xFF
    val green = (argb >> 8) & 0xFF
    val blue = argb & 0xFF
    RGBColor(red, green, blue, alpha)
  }

  val White = RGBColor(255, 255, 255)
  val Black = RGBColor(0, 0, 0)
}

/**
 * Red/Green/Blue
 */
case class RGBColor(red: Int, green: Int, blue: Int, alpha: Int = 255) extends Color {
  require(0 <= red && red <= 255, "Red component is invalid")
  require(0 <= green && green <= 255, "Green component is invalid")
  require(0 <= blue && blue <= 255, "Blue component is invalid")
  require(0 <= alpha && alpha <= 255, "Alpha component is invalid")

  /**
   * Returns as an int the value of this color. The RGB and alpha components are packed
   * into the int as byes.
   * @return
   */
  def toInt: Int = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | blue & 0xFF

  def toPixel: RGBPixel = RGBPixel(red, green, blue)

  override def toRGB: RGBColor = this

  /**
   * Returns a conversion of this color into a CYMK color.
   * If this colour is already a CYMK then the same instance will be returned.
   */
  override def toCYMK: CMYK = {
    val max: Float = Math.max(Math.max(red, green), blue) / 255f
    val k = 1.0f - max
    if (max > 0.0f) {
      CMYK(1.0f - (red / 255f) / max, 1.0f - (green / 255f) / max, 1.0f - (blue / 255f) / max, k)
    } else {
      CMYK(0, 0, 0, k)
    }
  }

  private def getHue(red: Float, green: Float, blue: Float, max: Float, min: Float): Float = {
    var hue = max - min
    if (hue > 0.0f) {
      if (max == red) {
        hue = (green - blue) / hue
        if (hue < 0.0f) {
          hue += 6.0f
        }
      } else if (max == green) {
        hue = 2.0f + (blue - red) / hue
      } else {
        hue = 4.0f + (red - green) / hue
      }
      hue /= 6.0f
    }
    hue
  }

  override def toHSV: HSVColor = {
    val max = Math.max(Math.max(red, green), blue) / 255f
    val min = Math.min(Math.min(red, green), blue) / 255f
    var saturation = max - min
    if (saturation > 0.0f) saturation = saturation / max
    HSVColor(getHue(red, green, blue, max, min), saturation, max, alpha)
  }

  override def toHSL: HSLColor = {
    val max = Math.max(Math.max(red, green), blue) / 255f
    val min = Math.min(Math.min(red, green), blue) / 255f
    val sum = max + min
    var saturation = max - min
    if (saturation > 0.0f) {
      saturation = saturation / (if (sum > 1.0f) 2.0f - sum else sum)
    }
    HSLColor(getHue(red, green, blue, max, min), saturation, sum / 2.0f, alpha)
  }

  /**
   * Returns a HEX String of this colour. Eg for 0,255,0, this method will return 00FF00.
   */
  override def toHex: String = Integer.toHexString(toInt & 0xffffff).toUpperCase.reverse.padTo(6, '0').reverse
}

case class CMYK(c: Float, m: Float, y: Float, k: Float) extends Color {
  override def toRGB: RGBColor = {
    val red = 1.0f + c * k - k - c
    val green = 1.0f + m * k - k - m
    val blue = 1.0f + y * k - k - y
    RGBColor((red * 255).toInt, (green * 255).toInt, (blue * 255).toInt, 0)
  }
  override def toCYMK: CMYK = this
}

/**
 * Hue/Saturation/Value
 *
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 */
case class HSVColor(hue: Float, saturation: Float, value: Float, alpha: Float) extends Color {
  require(0 <= hue && hue < 1f, "Hue component is invalid")
  require(0 <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0 <= value && value <= 1f, "Value component is invalid")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toHSV: HSVColor = this

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
 * Hue/Saturation/Lightness
 *
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 */
case class HSLColor(hue: Float, saturation: Float, lightness: Float, alpha: Float) extends Color {
  require(0 <= hue && hue <= 360f, "Hue component is invalid")
  require(0 <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0 <= lightness && lightness <= 1f, "Lightness component is invalid")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toHSL: HSLColor = this

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
