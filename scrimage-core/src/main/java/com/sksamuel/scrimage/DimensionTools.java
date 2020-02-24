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

package com.sksamuel.scrimage;

import java.awt.Color;

public class DimensionTools {

   public static final Color BG_COLOR = java.awt.Color.WHITE;
   public static final int SCALING_METHOD = java.awt.Image.SCALE_AREA_AVERAGING;

   public static Dimension dimensionsToCover(Dimension target, Dimension source) {

      double xscale = target.getX() / (double) source.getX();
      double yscale = target.getY() / (double) source.getY();

      if (xscale > yscale) {
         return new Dimension((int) Math.ceil(source.getX() * xscale), (int) Math.ceil(source.getY() * xscale));
      } else {
         return new Dimension((int) Math.ceil(source.getX() * yscale), (int) Math.ceil(source.getY() * yscale));
      }
   }

   /**
    * Returns width and height that allow the given source width, height to fit inside the target width, height
    * without losing aspect ratio
    */
   public static Dimension dimensionsToFit(Dimension target, Dimension source) {

      // if target width/height is zero then we have no preference for that, so set it to the original value,
      // since it cannot be any larger
      int maxWidth;
      if (target.getX() == 0) {
         maxWidth = source.getX();
      } else {
         maxWidth = target.getX();
      }

      int maxHeight;
      if (target.getY() == 0) {
         maxHeight = source.getY();
      } else {
         maxHeight = target.getY();
      }

      if (maxWidth * source.getY() < maxHeight * source.getX())
         return new Dimension(maxWidth, (int) (source.getY() * maxWidth / source.getX()));
      else
         return new Dimension((int) (source.getX() * maxHeight / source.getY()), maxHeight);
   }
}
