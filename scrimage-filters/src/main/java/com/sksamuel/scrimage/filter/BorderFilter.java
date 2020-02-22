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

import java.awt.*;

public class BorderFilter implements Filter {

    private final int borderWidth;
    private final Color color;

    public BorderFilter(int borderWidth, Color color) {
        this.borderWidth = borderWidth;
        this.color = color;
    }

    public BorderFilter(int borderWidth) {
        this(borderWidth, Color.BLACK);
    }

    @Override
    public void apply(ImmutableImage image) {
        Graphics2D g2 = (Graphics2D) image.awt().getGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, borderWidth, image.height); // left
        g2.fillRect(image.width - borderWidth, 0, borderWidth, image.height); // right
        g2.fillRect(0, 0, image.width, borderWidth); // top
        g2.fillRect(0, image.height - borderWidth, image.width, borderWidth); // bottom

    }
}
