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

import java.awt.*;
import java.awt.image.BufferedImageOp;

public class TritoneFilter extends BufferedOpFilter {

    private final int shadows;
    private final int midtones;
    private final int highlights;

    public TritoneFilter(int shadows, int midtones, int highlights) {
        this.shadows = shadows;
        this.midtones = midtones;
        this.highlights = highlights;
    }

    public TritoneFilter(Color shadows, Color midtones, Color highlights) {
        this.shadows = shadows.getRGB();
        this.midtones = midtones.getRGB();
        this.highlights = highlights.getRGB();
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.TritoneFilter op = new thirdparty.jhlabs.image.TritoneFilter();
        op.setShadowColor(shadows);
        op.setMidColor(midtones);
        op.setHighColor(highlights);
        return op;
    }
}
