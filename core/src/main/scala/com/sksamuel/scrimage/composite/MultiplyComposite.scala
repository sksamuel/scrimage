package com.sksamuel.scrimage.composite

import com.sksamuel.scrimage.{Image, Composite}
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite.BlendingMode
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel */
class BlenderComposite(mode: BlendingMode, alpha: Double) extends Composite {
    def apply(src: Image, applicative: Image) {
        val g2 = src.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.setComposite(BlendComposite.getInstance(mode, alpha.toFloat))
        g2.drawImage(applicative.awt, 0, 0, null)
    }
}
class MultiplyComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.MULTIPLY, alpha.toFloat)
class AverageComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.AVERAGE, alpha.toFloat)
class ColorComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR, alpha.toFloat)
class ColorBurnComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR_BURN, alpha.toFloat)
class ColorDodgeComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR_DODGE, alpha.toFloat)
class ScreenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SCREEN, alpha.toFloat)
class DifferenceComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.DIFFERENCE, alpha.toFloat)
class OverlayComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.OVERLAY, alpha.toFloat)
class NormalComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.NORMAL, alpha.toFloat)
class NegationComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.NEGATION, alpha.toFloat)
class SaturationComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SATURATION, alpha.toFloat)
class SubtractComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SUBTRACT, alpha.toFloat)
class HueComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HUE, alpha.toFloat)
class HardLightComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HARD_LIGHT, alpha.toFloat)
class HeatComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HEAT, alpha.toFloat)
class LuminosityComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.LUMINOSITY, alpha.toFloat)
class LightenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.LIGHTEN, alpha.toFloat)
class ReflectComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.REFLECT, alpha.toFloat)
class RedComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.RED, alpha.toFloat)
class GreenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.GREEN, alpha.toFloat)
class GlowComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.GLOW, alpha.toFloat)
class BlueComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.BLUE, alpha.toFloat)

