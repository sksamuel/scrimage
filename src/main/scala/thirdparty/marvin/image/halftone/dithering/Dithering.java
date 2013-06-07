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

package thirdparty.marvin.image.halftone.dithering;

import thirdparty.marvin.image.*;
import thirdparty.marvin.image.grayScale.GrayScale;

/**
 * Halftone dithering implementation.
 *
 * @author Gabriel Ambr�sio Archanjo
 * @version 1.0 02/28/2008
 */
public class Dithering extends MarvinAbstractImagePlugin {
    private static final int DOT_AREA = 5;
    private static final int arrDither[] = {167, 200, 230, 216, 181,
            94, 72, 193, 242, 232,
            36, 52, 222, 167, 200,
            181, 126, 210, 94, 72,
            232, 153, 111, 36, 52
    };

    public void load() {
    }

    public void process(
            MarvinImage a_imageIn,
            MarvinImage a_imageOut,
            MarvinAttributes a_attributesOut,
            MarvinImageMask a_mask,
            boolean a_previewMode
    ) {
        // Gray
        MarvinImagePlugin l_filter = new GrayScale();
        l_filter.process(a_imageIn, a_imageIn, a_attributesOut, a_mask, a_previewMode);

        boolean[][] l_arrMask = a_mask.getMaskArray();

        for (int x = 0; x < a_imageIn.getWidth(); x += DOT_AREA) {
            for (int y = 0; y < a_imageIn.getHeight(); y += DOT_AREA) {
                if (l_arrMask != null && !l_arrMask[x][y]) {
                    continue;
                }
                drawTone(x, y, a_imageIn, a_imageOut);
            }
        }
    }

    private void drawTone(int a_x, int a_y, MarvinImage a_imageIn, MarvinImage a_imageOut) {
        int l_grayIntensity;
        int l_x;
        int l_y;

        for (int x = 0; x < DOT_AREA * DOT_AREA; x++) {
            l_x = x % DOT_AREA;
            l_y = x / DOT_AREA;

            if (a_x + l_x < a_imageIn.getWidth() && a_y + l_y < a_imageIn.getHeight()) {

                l_grayIntensity = 255 - (a_imageIn.getIntComponent0(a_x + l_x, a_y + l_y));

                if (l_grayIntensity > arrDither[x]) {
                    a_imageOut.setIntColor(a_x + l_x, a_y + l_y, a_imageIn.getAlphaComponent(a_x + l_x, a_y + l_y), 0, 0, 0);
                } else {
                    a_imageOut.setIntColor(a_x + l_x, a_y + l_y, a_imageIn.getAlphaComponent(a_x + l_x, a_y + l_y), 255, 255, 255);
                }
            }
        }
    }
}