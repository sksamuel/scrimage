package thirdparty.marvin.image.color;

/**
 Marvin Project <2007-2012>

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


import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

/**
 * Invert the pixels color to create an emboss effect.
 *
 * @author Chris Mack
 * @version 1.0 12/07/2011
 */
public class Emboss extends MarvinAbstractImagePlugin {
    public void load() {
    }

    public void process
            (
                    MarvinImage a_imageIn,
                    MarvinImage a_imageOut,
                    MarvinAttributes a_attributesOut,
                    MarvinImageMask a_mask,
                    boolean a_previewMode
            ) {
        boolean[][] l_arrMask = a_mask.getMaskArray();

        for (int x = 0; x < a_imageIn.getWidth(); x++) {
            for (int y = 0; y < a_imageIn.getHeight(); y++) {
                if (l_arrMask != null && !l_arrMask[x][y]) {
                    a_imageOut.setIntColor(x, y, a_imageIn.getIntColor(x, y));
                    continue;
                }

                int rDiff = 0;
                int gDiff = 0;
                int bDiff = 0;

                if (y > 0 && x > 0) {

                    // Red component difference between the current and the upperleft pixels
                    rDiff = a_imageIn.getIntComponent0(x, y) - a_imageIn.getIntComponent0(x - 1, y - 1);

                    // Green component difference between the current and the upperleft pixels
                    gDiff = a_imageIn.getIntComponent1(x, y) - a_imageIn.getIntComponent1(x - 1, y - 1);

                    // Blue component difference between the current and the upperleft pixels
                    bDiff = a_imageIn.getIntComponent2(x, y) - a_imageIn.getIntComponent2(x - 1, y - 1);

                } else {
                    rDiff = 0;
                    gDiff = 0;
                    bDiff = 0;
                }

                int diff = rDiff;
                if (Math.abs(gDiff) > Math.abs(diff))
                    diff = gDiff;
                if (Math.abs(bDiff) > Math.abs(diff))
                    diff = bDiff;

                int grayLevel = Math.max(Math.min(128 + diff, 255), 0);

                a_imageOut.setIntColor(x, y, grayLevel, grayLevel, grayLevel);
            }
        }
    }
}