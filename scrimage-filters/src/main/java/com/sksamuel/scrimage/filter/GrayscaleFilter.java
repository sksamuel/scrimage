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

public class GrayscaleFilter implements Filter {

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
         int gray = (int) Math.round(0.2126 * r + 0.7152 * g + 0.0722 * b);
         argb[i] = (a << 24) | (gray << 16) | (gray << 8) | gray;
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
