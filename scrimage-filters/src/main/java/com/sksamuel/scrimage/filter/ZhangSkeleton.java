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
import thirdparty.jhlabs.image.PixelUtils;

/**
 * Reduces a shape to a one-pixel-wide skeleton using the Zhang-Suen thinning
 * algorithm (T. Y. Zhang and C. Y. Suen, "A fast parallel algorithm for
 * thinning digital patterns", CACM, March 1984, 236-239).
 * <p>
 * The image is first binarised: a pixel is treated as foreground when its
 * brightness is below {@code threshold} (default 128), i.e. dark shapes on a
 * lighter background. The foreground is then repeatedly thinned — boundary
 * pixels are stripped in two complementary sub-iterations per pass until no
 * more can be removed without breaking connectivity or eroding an endpoint.
 * <p>
 * The result is written as an opaque black skeleton on a white background.
 * Pixels on the image edge are thinned correctly (out-of-image neighbours are
 * treated as background), and every output pixel is written, so the filter is
 * safe on images of any size, including 1xN, Nx1 and 1x1.
 */
public class ZhangSkeleton implements Filter {

   private final int threshold;

   /**
    * Creates a skeleton filter with the default brightness threshold of 128.
    */
   public ZhangSkeleton() {
      this(128);
   }

   /**
    * @param threshold the brightness cut-off in [0, 255]; a pixel is foreground
    *                  when its brightness is strictly below this value.
    */
   public ZhangSkeleton(int threshold) {
      if (threshold < 0 || threshold > 255)
         throw new IllegalArgumentException("threshold must be in [0, 255], got " + threshold);
      this.threshold = threshold;
   }

   @Override
   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      int[] argb = image.awt().getRGB(0, 0, w, h, null, 0, w);

      // Binarise into 1 = foreground (dark), 0 = background.
      byte[] fg = new byte[w * h];
      for (int i = 0; i < argb.length; i++) {
         fg[i] = (byte) (PixelUtils.brightness(argb[i]) < threshold ? 1 : 0);
      }

      thin(fg, w, h);

      // Write back: skeleton -> opaque black, everything else -> opaque white.
      for (int i = 0; i < argb.length; i++) {
         argb[i] = fg[i] != 0 ? 0xff000000 : 0xffffffff;
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }

   private static void thin(byte[] fg, int w, int h) {
      int[] toRemove = new int[w * h];
      boolean changed;
      do {
         changed = false;
         for (int step = 0; step < 2; step++) {
            int n = 0;
            for (int y = 0; y < h; y++) {
               for (int x = 0; x < w; x++) {
                  if (fg[y * w + x] == 0)
                     continue;

                  // 8 neighbours, clockwise from north; out-of-image == background.
                  int p2 = at(fg, w, h, x, y - 1);
                  int p3 = at(fg, w, h, x + 1, y - 1);
                  int p4 = at(fg, w, h, x + 1, y);
                  int p5 = at(fg, w, h, x + 1, y + 1);
                  int p6 = at(fg, w, h, x, y + 1);
                  int p7 = at(fg, w, h, x - 1, y + 1);
                  int p8 = at(fg, w, h, x - 1, y);
                  int p9 = at(fg, w, h, x - 1, y - 1);

                  int b = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                  if (b < 2 || b > 6)
                     continue;

                  // A = number of 0 -> 1 transitions in the ordered ring.
                  int a = 0;
                  if (p2 == 0 && p3 == 1) a++;
                  if (p3 == 0 && p4 == 1) a++;
                  if (p4 == 0 && p5 == 1) a++;
                  if (p5 == 0 && p6 == 1) a++;
                  if (p6 == 0 && p7 == 1) a++;
                  if (p7 == 0 && p8 == 1) a++;
                  if (p8 == 0 && p9 == 1) a++;
                  if (p9 == 0 && p2 == 1) a++;
                  if (a != 1)
                     continue;

                  if (step == 0) {
                     if (p2 * p4 * p6 != 0) continue; // at least one of N, E, S is background
                     if (p4 * p6 * p8 != 0) continue; // at least one of E, S, W is background
                  } else {
                     if (p2 * p4 * p8 != 0) continue; // at least one of N, E, W is background
                     if (p2 * p6 * p8 != 0) continue; // at least one of N, S, W is background
                  }

                  toRemove[n++] = y * w + x;
               }
            }
            for (int k = 0; k < n; k++)
               fg[toRemove[k]] = 0;
            if (n > 0)
               changed = true;
         }
      } while (changed);
   }

   private static int at(byte[] fg, int w, int h, int x, int y) {
      if (x < 0 || x >= w || y < 0 || y >= h)
         return 0;
      return fg[y * w + x];
   }
}
