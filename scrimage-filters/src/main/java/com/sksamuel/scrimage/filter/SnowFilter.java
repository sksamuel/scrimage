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
import com.sksamuel.scrimage.ScaleMethod;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SnowFilter implements Filter {

   private static final ImmutableImage snow;

   static {
      try {
         snow = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/filter/snow1.jpg");
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void apply(ImmutableImage image) {
      ImmutableImage scaled = ImmutableImage.wrapAwt(snow.scaleTo(image.width, image.height, ScaleMethod.Bicubic).awt(), BufferedImage.TYPE_INT_ARGB);

      ImmutableImage copy;
      if (image.getType() == BufferedImage.TYPE_INT_ARGB || image.getType() == BufferedImage.TYPE_INT_RGB) {
         copy = image;
      } else {
         copy = image.copy(BufferedImage.TYPE_INT_ARGB);
      }

      Graphics2D g2 = (Graphics2D) copy.awt().getGraphics();
      g2.setComposite(new BlendComposite(BlendingMode.SCREEN, 1.0f));
      g2.drawImage(scaled.awt(), 0, 0, null);
      g2.dispose();
   }
}
