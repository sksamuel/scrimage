/*
Copyright 2006 Jerry Huxtable

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

package thirdparty.jhlabs.image;

import java.awt.geom.*;
import java.awt.image.*;

/**
 * A Filter which produces the effect of looking into a kaleidoscope.
 */
public class KaleidoscopeFilter extends TransformFilter {

   private float angle = 0;
   private float angle2 = 0;
   private float centreX = 0.5f;
   private float centreY = 0.5f;
   private int sides = 3;
   private float radius = 0;

   private float icentreX;
   private float icentreY;

   /**
    * Construct a KaleidoscopeFilter with no distortion.
    */
   public KaleidoscopeFilter() {
      setEdgeAction(CLAMP);
   }

   /**
    * Set the number of sides of the kaleidoscope.
    *
    * @param sides the number of sides
    */
   public void setSides(int sides) {
      this.sides = sides;
   }

   public void setAngle(float angle) {
      this.angle = angle;
   }

   public void setCentreX(float centreX) {
      this.centreX = centreX;
   }

   public void setCentreY(float centreY) {
      this.centreY = centreY;
   }

   public void setRadius(float radius) {
      this.radius = radius;
   }

   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
      icentreX = src.getWidth() * centreX;
      icentreY = src.getHeight() * centreY;
      return super.filter(src, dst);
   }

   protected void transformInverse(int x, int y, float[] out) {
      double dx = x - icentreX;
      double dy = y - icentreY;
      double r = Math.sqrt(dx * dx + dy * dy);
      double theta = Math.atan2(dy, dx) - angle - angle2;
      theta = ImageMath.triangle((float) (theta / Math.PI * sides * .5));
      if (radius != 0) {
         double c = Math.cos(theta);
         double radiusc = radius / c;
         r = radiusc * ImageMath.triangle((float) (r / radiusc));
      }
      theta += angle;

      out[0] = (float) (icentreX + r * Math.cos(theta));
      out[1] = (float) (icentreY + r * Math.sin(theta));
   }

   public String toString() {
      return "Distort/Kaleidoscope...";
   }

}
