/**
 Marvin Project <2007-2009>

 Initial version by:

 Danilo Rosetto Munoz
 Fabio Andrijauskas
 Gabriel Ambrosio Archanjo

 site: http://marvinproject.sourceforge.net

 GPL
 Copyright (C) <2007>

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package thirdparty.marvin.image.halftone.circles;


import thirdparty.marvin.image.*;
import thirdparty.marvin.image.grayScale.GrayScale;

import java.awt.*;

/**
 * Halftone using circles.
 *
 * @author Gabriel Ambr�sio Archanjo
 * @version 1.0 02/28/2008
 */
public class Circles extends MarvinAbstractImagePlugin {
    private int circleWidth;
    private int shift;
    private int circlesDistance;


    private MarvinAttributes attributes;

    public void load() {
        attributes = getAttributes();
        attributes.set("circleWidth", 6);
        attributes.set("shift", 0);
        attributes.set("circlesDistance", 0);
    }

    public void process
            (
                    MarvinImage a_imageIn,
                    MarvinImage a_imageOut,
                    MarvinAttributes a_attributesOut,
                    MarvinImageMask a_mask,
                    boolean a_previewMode
            ) {
        double l_intensity;

        circleWidth = (Integer) attributes.get("circleWidth");
        shift = (Integer) attributes.get("shift");
        circlesDistance = (Integer) attributes.get("circlesDistance");

        Graphics l_graphics = a_imageOut.getBufferedImage().getGraphics();

        // Gray
        MarvinImagePlugin l_filter = new GrayScale();
        l_filter.process(a_imageIn, a_imageIn, a_attributesOut, a_mask, a_previewMode);


        boolean[][] l_arrMask = a_mask.getMaskArray();

        int l_dif = 0;
        for (int y = 0; y < a_imageIn.getHeight(); y += circleWidth + circlesDistance) {
            for (int x = 0 + l_dif; x < a_imageIn.getWidth(); x += circleWidth + circlesDistance) {
                if (l_arrMask != null && !l_arrMask[x][y]) {
                    continue;
                }
                l_intensity = getSquareIntensity(x, y, a_imageIn);
                l_intensity += 1.0 / circleWidth;
                l_graphics.setColor(Color.white);
                l_graphics.fillRect(x, y, circleWidth + circlesDistance, circleWidth + circlesDistance);
                l_graphics.setColor(Color.black);
                l_graphics.fillArc((int) (x + (circleWidth - (l_intensity * circleWidth)) / 2), (int) (y + (circleWidth - (l_intensity * circleWidth)) / 2), (int) (l_intensity * (circleWidth)), (int) (l_intensity * (circleWidth)), 1, 360);
            }
            l_dif = (l_dif + shift) % circleWidth;
        }
        a_imageOut.updateColorArray();
    }

    private double getSquareIntensity(int a_x, int a_y, MarvinImage image) {
        double l_totalValue = 0;
        double l_pixels = 0;
        for (int y = 0; y < circleWidth; y++) {
            for (int x = 0; x < circleWidth; x++) {
                if (a_x + x > 0 && a_x + x < image.getWidth() && a_y + y > 0 && a_y + y < image.getHeight()) {
                    l_pixels++;
                    l_totalValue += 255 - (image.getIntComponent0(a_x + x, a_y + y));
                }
            }
        }
        return (l_totalValue / (circleWidth * circleWidth * 255));
    }
}