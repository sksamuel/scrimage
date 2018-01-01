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

import com.sksamuel.scrimage.Color;
import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.RGBColor;

import java.awt.*;

public class ColorizeFilter implements Filter {

    private final Color color;

    public ColorizeFilter(Color color) {
        this.color = color;
    }

    public ColorizeFilter(int r, int g, int b) {
        this(r, g, b, 127);
    }

    public ColorizeFilter(int r, int g, int b, int a) {
        this(new RGBColor(r, g, b, a));
    }

    @Override
    public void apply(Image image) {
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        g2.setColor(color.toAWT());
        g2.fillRect(0, 0, image.width, image.height);
        g2.dispose();
    }
}
