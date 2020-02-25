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

public class SummerFilter implements Filter {

    private static final ImmutableImage summer;

    static {
        try {
            summer = ImmutableImage.fromResource("/com/sksamuel/scrimage/filter/summer1.jpg", ImmutableImage.DEFAULT_DATA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final boolean vignette;

    public SummerFilter(boolean vignette) {
        this.vignette = vignette;
    }

    @Override
    public void apply(ImmutableImage image) throws IOException {
        ImmutableImage scaled = ImmutableImage.wrapAwt(summer.scaleTo(image.width, image.height, ScaleMethod.Bicubic).awt(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        g2.setComposite(BlendComposite.getInstance(BlendingMode.INVERSE_COLOR_BURN, 0.5f));
        g2.drawImage(scaled.awt(), 0, 0, null);
        g2.dispose();
        if (vignette)
            new VignetteFilter(0.92f, 0.98f, 0.3f, Color.BLACK).apply(image);
    }
}
