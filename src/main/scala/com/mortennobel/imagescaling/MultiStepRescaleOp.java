/*
 * Copyright 2009, Morten Nobel-Joergensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mortennobel.imagescaling;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This code is very inspired on Chris Campbells article "The Perils of Image.getScaledInstance()"
 *
 * The article can be found here:
 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
 *
 * Note that the filter method is threadsafe
 */
public class MultiStepRescaleOp extends AdvancedResizeOp {
	private final Object renderingHintInterpolation;

	public MultiStepRescaleOp(int dstWidth, int dstHeight) {
		this (dstWidth, dstHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	public MultiStepRescaleOp(int dstWidth, int dstHeight, Object renderingHintInterpolation) {
		this(DimensionConstrain.createAbsolutionDimension(dstWidth, dstHeight), renderingHintInterpolation);
	}

	public MultiStepRescaleOp(DimensionConstrain dimensionConstain) {
		this (dimensionConstain, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	public MultiStepRescaleOp(DimensionConstrain dimensionConstain, Object renderingHintInterpolation) {
		super(dimensionConstain);
		this.renderingHintInterpolation = renderingHintInterpolation;
		assert RenderingHints.KEY_INTERPOLATION.isCompatibleValue(renderingHintInterpolation) :
				"Rendering hint "+renderingHintInterpolation+" is not compatible with interpolation";
	}


	public BufferedImage doFilter(BufferedImage img, BufferedImage dest, int dstWidth, int dstHeight) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;

		// Use multi-step technique: start with original size, then
		// scale down in multiple passes with drawImage()
		// until the target size is reached
		w = img.getWidth();
		h = img.getHeight();

        do {
            if (w > dstWidth) {
                w /= 2;
                if (w < dstWidth) {
                    w = dstWidth;
                }
            } else {
                w = dstWidth;
            }

            if (h > dstHeight) {
                h /= 2;
                if (h < dstHeight) {
                    h = dstHeight;
                }
            } else {
                h = dstHeight;
            }

            BufferedImage tmp;
			if (dest!=null && dest.getWidth()== w && dest.getHeight()== h && w==dstWidth && h==dstHeight){
				tmp = dest;
			} else {
				tmp = new BufferedImage(w,h,type);
			}
			Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, renderingHintInterpolation);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != dstWidth || h != dstHeight);

        return ret;
    }
}