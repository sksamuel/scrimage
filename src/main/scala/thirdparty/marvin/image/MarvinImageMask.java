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

package thirdparty.marvin.image;

/**
 * Mask indicating what pixels must be processed.
 *
 * @author Gabriel Ambrosio Archanjo
 */
public class MarvinImageMask {
    // Static nullMask
    public static MarvinImageMask NULL_MASK = new MarvinImageMask();

    private boolean arrMask[][];
    private int width,
            height;

    /**
     * Constructor for null mask
     */
    public MarvinImageMask() {
        arrMask = null;
    }

    /**
     * Contructor
     *
     * @param w width of the image referenced by the mask
     * @param h height of the image referenced by the mask
     */
    public MarvinImageMask(int w, int h) {
        width = w;
        height = h;
        arrMask = new boolean[width][height];
    }

    public MarvinImageMask(boolean mask[][]) {
        arrMask = mask;
        width = arrMask[0].length;
        height = arrMask.length;
    }

    /**
     * @param maskWidth    width of the image referenced by the mask
     * @param maskHeight   height of the image referenced by the mask
     * @param startX       start pixel of the region in x axes
     * @param startY       start pixel of the region in y axes
     * @param regionWidth  width of the region
     * @param regionHeight height of the region
     */
    public MarvinImageMask(int maskWidth,
                           int maskHeight,
                           int startX,
                           int startY,
                           int regionWidth,
                           int regionHeight) {
        this(maskWidth, maskHeight);
        addRectRegion(startX, startY, regionWidth, regionHeight);
    }

    /**
     * Add a point to the mask.
     *
     * @param x
     * @param y
     */
    public void addPoint(int x, int y) {
        arrMask[x][y] = true;
    }

    /**
     * Remove point from the mask.
     *
     * @param x
     * @param y
     */
    public void removePoint(int x, int y) {
        arrMask[x][y] = false;
    }

    /**
     * Clear the mask for a new selection
     */
    public void clear() {
        if (arrMask != null) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    arrMask[x][y] = false;
                }
            }
        }
    }

    /**
     * @return Mask Array.
     */
    public boolean[][] getMaskArray() {
        return arrMask;
    }

    /**
     * @param startX       Start pixel of the region in x axes
     * @param startY       Start pixel of the region in y axes
     * @param regionWidth  Width of the region
     * @param regionHeight Height of the region
     */
    public void addRectRegion(int startX,
                              int startY,
                              int regionWidth,
                              int regionHeight) {
        for (int x = startX; x < startX + regionWidth; x++) {
            for (int y = startY; y < startY + regionHeight; y++) {
                arrMask[x][y] = true;
            }
        }
    }
}
