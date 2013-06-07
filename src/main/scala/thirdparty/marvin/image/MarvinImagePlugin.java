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

public interface MarvinImagePlugin extends MarvinPlugin {

    /**
     * Executes the algorithm.
     *
     * @param imgIn       input image.
     * @param imgOut      output image.
     * @param attrOut     output attributes.
     * @param mask        mask containing what pixels should be considered.
     * @param previewMode it is or isn�t on preview mode.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut,
            MarvinAttributes attrOut,
            MarvinImageMask mask,
            boolean previewMode
    );

    /**
     * Executes the algorithm.
     *
     * @param imgIn   input image.
     * @param imgOut  output image.
     * @param attrOut output attributes.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut,
            MarvinImageMask mask
    );

    public void process
            (
                    MarvinImage imgIn,
                    MarvinImage imgOut,
                    MarvinAttributes attrOut
            );

    /**
     * Executes the algorithm.
     *
     * @param imgIn  input image.
     * @param imgOut output image.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut
    );
}