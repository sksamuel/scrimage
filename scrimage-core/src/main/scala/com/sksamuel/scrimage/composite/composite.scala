/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.composite

import com.sksamuel.scrimage.{ Image, Composite }
import java.awt.Graphics2D
import thirdparty.romainguy.BlendComposite.BlendingMode
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel */
class BlenderComposite(mode: BlendingMode, alpha: Double) extends Composite {
  def apply(src: Image, applicative: Image) {
    val g2 = src.awt.getGraphics.asInstanceOf[Graphics2D]
    g2.setComposite(BlendComposite.getInstance(mode, alpha.toFloat))
    g2.drawImage(applicative.awt, 0, 0, null)
    g2.dispose()
  }
}
class AverageComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.AVERAGE, alpha.toFloat)
class BlueComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.BLUE, alpha.toFloat)
class ColorComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR, alpha.toFloat)
class ColorBurnComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR_BURN, alpha.toFloat)
class ColorDodgeComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.COLOR_DODGE, alpha.toFloat)
class DifferenceComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.DIFFERENCE, alpha.toFloat)
class GreenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.GREEN, alpha.toFloat)
class GlowComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.GLOW, alpha.toFloat)
class HueComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HUE, alpha.toFloat)
class HardLightComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HARD_LIGHT, alpha.toFloat)
class HeatComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.HEAT, alpha.toFloat)
class LightenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.LIGHTEN, alpha.toFloat)
class LuminosityComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.LUMINOSITY, alpha.toFloat)
class MultiplyComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.MULTIPLY, alpha.toFloat)
class NegationComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.NEGATION, alpha.toFloat)
class OverlayComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.OVERLAY, alpha.toFloat)
class RedComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.RED, alpha.toFloat)
class ReflectComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.REFLECT, alpha.toFloat)
class SaturationComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SATURATION, alpha.toFloat)
class ScreenComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SCREEN, alpha.toFloat)
class SubtractComposite(alpha: Double) extends BlenderComposite(BlendComposite.BlendingMode.SUBTRACT, alpha.toFloat)

