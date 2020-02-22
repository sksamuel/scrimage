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

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.ImmutableImage;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CausticsFilter implements Filter {

    private final float amount;
    private final float turbulence;
    private final float alpha;
    private final int backgroundColor;

    public CausticsFilter(float amount, float turbulence, float alpha, int backgroundColor) {
        this.amount = amount;
        this.turbulence = turbulence;
        this.alpha = alpha;
        this.backgroundColor = backgroundColor;
    }

    public CausticsFilter() {
        this(1.0f, 1.0f, 0.2f, 0xff799fff);
    }

    @Override
    public void apply(ImmutableImage image) {

        thirdparty.jhlabs.image.CausticsFilter op = new thirdparty.jhlabs.image.CausticsFilter();
        op.setAmount(amount);
        op.setTurbulence(turbulence);
        op.setSamples(2);
        op.setTime(0);
        op.setBgColor(backgroundColor);

        BufferedImage caustics = op.filter(image.awt(), null);

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        Graphics2D g2 = image.awt().createGraphics();
        g2.setComposite(composite);
        g2.drawImage(caustics, 0, 0, null);
        g2.dispose();
    }
}