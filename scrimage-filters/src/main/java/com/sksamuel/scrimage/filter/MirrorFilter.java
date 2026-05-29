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

/**
 * A filter that mirrors (flips) the image left to right, so the leftmost column
 * becomes the rightmost and vice versa. The image dimensions are unchanged.
 */
public class MirrorFilter implements Filter {

   @Override
   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      int[] argb = image.awt().getRGB(0, 0, w, h, null, 0, w);
      int half = w / 2;
      for (int y = 0; y < h; y++) {
         int row = y * w;
         for (int x = 0; x < half; x++) {
            int left = row + x;
            int right = row + (w - 1 - x);
            int tmp = argb[left];
            argb[left] = argb[right];
            argb[right] = tmp;
         }
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
