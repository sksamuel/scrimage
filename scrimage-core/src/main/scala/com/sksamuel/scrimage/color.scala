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
  def toCMYK: CMYKColor = toRGB.toCMYK

  def toHSV: HSVColor = toRGB.toHSV

  def toHSL: HSLColor = toRGB.toHSL

  /**
   * Returns a HEX String of this colour. Eg for 0,255,0, this method will return 00FF00.
   */
  def toHex: String = Integer.toHexString(toRGB.toInt & 0xffffff).toUpperCase.reverse.padTo(6, '0').reverse

  /**
   * Returns this instance as a java.awt.Color.
   * AWT Colors use the RGB color model.
   */
  def toAWT: java.awt.Color = {
    val rgb = toRGB
    new java.awt.Color(rgb.red, rgb.green, rgb.blue, rgb.alpha)
  }

  def toPixel: Pixel = {
    val rgb = toRGB
    ARGBIntPixel(rgb.red, rgb.green, rgb.blue, rgb.alpha)
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
 *
 * The red, green, blue, and alpha components should be between [0,255].
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

  override def toRGB: RGBColor = this

  /**
   * Returns a conversion of this color into a CYMK color.
   * If this colour is already a CYMK then the same instance will be returned.
   */
  override def toCMYK: CMYKColor = {
    val max: Float = Math.max(Math.max(red, green), blue) / 255f
    val k = 1.0f - max
    if (max > 0.0f) {
      CMYKColor(1.0f - (red / 255f) / max, 1.0f - (green / 255f) / max, 1.0f - (blue / 255f) / max, k)
    } else {
      CMYKColor(0, 0, 0, k)
    }
  }

  // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
  override def toHSV: HSVColor = {

    val r = red / 255f
    val g = green / 255f
    val b = blue / 255f

    val max = Set(r, g, b).max
    val min = Set(r, g, b).min

    val d = max - min
    val s = if (max == 0) 0 else d / max
    val v = max

    val h = if (max == min) {
      // achromatic
      0f
    } else {
      val h = max match {
        case `r` => (g - b) / d + (if (g < b) 6 else 0)
        case `g` => (b - r) / d + 2;
        case `b` => (r - g) / d + 4;
      }
      h / 6f
    }

    HSVColor(h * 360f, s, v, 1f)
  }

  // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
  override def toHSL: HSLColor = {

    val r = red / 255f
    val g = green / 255f
    val b = blue / 255f

    val max = Set(r, g, b).max
    val min = Set(r, g, b).min

    val l = (max + min) / 2f

    val (h: Float, s: Float) = if (max == min) {
      // achromatic
      (0, 0)
    } else {
      val d = max - min
      val s = if (l > 0.5) d / (2 - max - min) else d / (max + min)
      val h = max match {
        case `r` => (g - b) / d + (if (g < b) 6 else 0)
        case `g` => (b - r) / d + 2
        case `b` => (r - g) / d + 4
      }
      (h / 6, s)
    }

    HSLColor(h * 360f, s, l, 1f)
  }
}

case class CMYKColor(c: Float, m: Float, y: Float, k: Float) extends Color {
  require(0 <= c && c <= 1f, s"cyan component is invalid $c")
  require(0 <= m && m <= 1f, s"magenta component is invalid $m")
  require(0 <= y && y <= 1f, s"yellow component is invalid $y")
  require(0 <= k && k <= 1f, s"black component is invalid $k")

  override def toRGB: RGBColor = {
    val red = 1.0f + c * k - k - c
    val green = 1.0f + m * k - k - m
    val blue = 1.0f + y * k - k - y
    RGBColor(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), 255)
  }
  override def toCMYK: CMYKColor = this
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
  require(0 <= hue && hue <= 360f, "Hue component is invalid")
  require(0 <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0 <= value && value <= 1f, "Value component is invalid")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toHSV: HSVColor = this

  // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
  override def toRGB: RGBColor = {

    // assumes h is in th range [0,1] not [0,360) so must convert
    val h = hue / 360f
    val s = saturation
    val v = value

    val i: Float = Math.floor(h * 6f).toFloat
    val f: Float = h * 6f - i
    val p: Float = v * (1 - s)
    val q: Float = v * (1 - f * s)
    val t: Float = v * (1 - (1 - f) * s)

    val (r, g, b) = i % 6 match {
      case 0 => (v, t, p)
      case 1 => (q, v, p)
      case 2 => (p, v, t)
      case 3 => (p, q, v)
      case 4 => (t, p, v)
      case 5 => (v, p, q)
      case _ => throw new RuntimeException(s"Cannot convert from HSV to RGB ($this)")
    }

    RGBColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), 255)
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
  require(0 <= hue && hue <= 360f, s"Hue component is invalid $hue")
  require(0 <= saturation && saturation <= 1f, s"Saturation component is invalid $saturation")
  require(0 <= lightness && lightness <= 1f, s"Lightness component is invalid $lightness")
  require(0 <= alpha && alpha <= 1f, "Alpha component is invalid")

  override def toHSL: HSLColor = this

  // credit to https://github.com/mjackson/mjijackson.github.com/blob/master/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript.txt
  override def toRGB: RGBColor = {

    // assumes h is in th range [0,1] not [0,360) so must convert
    val h = hue / 360f

    def hue2rgb(p: Float, q: Float, t: Float): Float = {
      val tprime: Float = if (t < 0) t + 1f else if (t > 1f) t - 1f else t
      if (tprime < 1f / 6f) p + (q - p) * 6f * tprime
      else if (tprime < 1f / 2f) q
      else if (tprime < 2f / 3f) p + (q - p) * (2f / 3f - tprime) * 6f
      else p
    }

    if (saturation == 0) {
      // achromatic
      RGBColor(
        Math.round(lightness * 255),
        Math.round(lightness * 255),
        Math.round(lightness * 255),
        Math.round(alpha * 255)
      )
    } else {
      val q = if (lightness < 0.5f) lightness * (1f + saturation) else lightness + saturation - lightness * saturation
      val p = 2f * lightness - q
      val r = hue2rgb(p, q, h + 1f / 3f)
      val g = hue2rgb(p, q, h)
      val b = hue2rgb(p, q, h - 1f / 3f)
      RGBColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), 255)
    }
  }
}
