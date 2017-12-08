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

package thirdparty.marvin.image.grayScale;

import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

/**
 * Represents an image in gray scale.
 *
 * @author F�bio Andrijauskas
 * @version 1.0 02/28/2008
 */
public class GrayScale extends MarvinAbstractImagePlugin {

    public void process(MarvinImage imageIn,
                        MarvinImage imageOut,
                        MarvinAttributes attributesOut,
                        MarvinImageMask mask,
                        boolean previewModes) {
        // Mask
        boolean[][] l_arrMask = mask.getMaskArray();

        int r, g, b, finalColor;
        for (int x = 0; x < imageIn.getWidth(); x++) {
            for (int y = 0; y < imageIn.getHeight(); y++) {
                if (l_arrMask != null && !l_arrMask[x][y]) {
                    continue;
                }
                //Red - 30% / Green - 59% / Blue - 11%
                r = imageIn.getIntComponent0(x, y);
                g = imageIn.getIntComponent1(x, y);
                b = imageIn.getIntComponent2(x, y);
                finalColor = (int) ((r * 0.3) + (g * 0.59) + (b * 0.11));
                imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), finalColor, finalColor, finalColor);

            }
        }
    }
}

