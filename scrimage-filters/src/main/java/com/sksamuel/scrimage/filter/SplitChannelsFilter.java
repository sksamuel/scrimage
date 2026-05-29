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
 * Keeps or removes individual colour channels. Each of the red, green and blue
 * flags determines whether that channel is retained (true) or zeroed out
 * (false). The alpha channel is always preserved.
 * <p>
 * For example {@code new SplitChannelsFilter(true, false, false)} keeps only the
 * red channel, producing the red component of the image.
 */
public class SplitChannelsFilter implements Filter {

   private final boolean red;
   private final boolean green;
   private final boolean blue;

   public SplitChannelsFilter(boolean red, boolean green, boolean blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   @Override
   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      int[] argb = image.awt().getRGB(0, 0, w, h, null, 0, w);
      // Preserve alpha; keep each colour channel only if its flag is set.
      int mask = 0xFF000000
         | (red ? 0x00FF0000 : 0)
         | (green ? 0x0000FF00 : 0)
         | (blue ? 0x000000FF : 0);
      for (int i = 0; i < argb.length; i++) {
         argb[i] &= mask;
      }
      image.awt().setRGB(0, 0, w, h, argb, 0, w);
   }
}
