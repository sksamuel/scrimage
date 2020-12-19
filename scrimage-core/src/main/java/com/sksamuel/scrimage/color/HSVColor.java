package com.sksamuel.scrimage.color;

import java.util.Objects;

/**
 * Hue/Saturation/Value
 * <p>
 * The hue component should be between 0.0 and 360.0
 * The saturation component should be between 0.0 and 1.0
 * The lightness component should be between 0.0 and 1.0
 * The alpha component should be between 0.0 and 1.0
 */
public class HSVColor implements Color {

   public final float hue;
   public final float saturation;
   public final float value;
   public final float alpha;

   public HSVColor(float hue, float saturation, float value, float alpha) {
      assert (0 <= hue && hue <= 360f);
      assert (0 <= saturation && saturation <= 1f);
      assert (0 <= value && value <= 1f);
      assert (0 <= alpha && alpha <= 1f);

      this.hue = hue;
      this.saturation = saturation;
      this.value = value;
      this.alpha = alpha;
   }

   public HSVColor toHSV() {
      return this;
   }

   public RGBColor toRGB() {
      int rgb = java.awt.Color.HSBtoRGB(hue / 360f, saturation, value);
      return RGBColor.fromRGBInt(rgb);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      HSVColor hsvColor = (HSVColor) o;
      return Float.compare(hsvColor.hue, hue) == 0 &&
         Float.compare(hsvColor.saturation, saturation) == 0 &&
         Float.compare(hsvColor.value, value) == 0 &&
         Float.compare(hsvColor.alpha, alpha) == 0;
   }

   @Override
   public int hashCode() {
      return Objects.hash(hue, saturation, value, alpha);
   }

   @Override
   public String toString() {
      final StringBuffer sb = new StringBuffer("HSVColor{");
      sb.append("hue=").append(hue);
      sb.append(", saturation=").append(saturation);
      sb.append(", value=").append(value);
      sb.append(", alpha=").append(alpha);
      sb.append('}');
      return sb.toString();
   }
}
