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
package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

public class OpacityFilter implements Filter {

   private final float amount;

   public OpacityFilter(float amount) {
      this.amount = amount;
   }

   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      int[] argb = image.awt().getRGB(0, 0, w, h, null, 0, w);
      for (int i = 0; i < argb.length; i++) {
         int p = argb[i];
         int a = (p >>> 24) & 0xFF;
         int r = (p >> 16) & 0xFF;
         int g = (p >> 8) & 0xFF;
         int b = p & 0xFF;
         int nr = (int) (r + (255 - r) * amount);
         int ng = (int) (g + (255 - g) * amount);
         int nb = (int) (b + (255 - b) * amount);
         argb[i] = (a << 24) | (nr << 16) | (ng << 8) | nb;
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
