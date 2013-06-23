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
package thirdparty.mortennobel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * @author Morten Nobel-Joergensen
 */
public abstract class AdvancedResizeOp {

    private final DimensionConstrain dimensionConstrain;

    public AdvancedResizeOp(DimensionConstrain dimensionConstrain) {
        this.dimensionConstrain = dimensionConstrain;
    }

    public final BufferedImage filter(BufferedImage src, BufferedImage dest) {
        Dimension dstDimension = dimensionConstrain.getDimension(new Dimension(src.getWidth(), src.getHeight()));
        int dstWidth = dstDimension.width;
        int dstHeight = dstDimension.height;
        return doFilter(src, dest, dstWidth, dstHeight);
    }

    protected abstract BufferedImage doFilter(BufferedImage src, BufferedImage dest, int dstWidth, int dstHeight);

    public final Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public final BufferedImage createCompatibleDestImage(BufferedImage src,
                                                         ColorModel destCM) {
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        return new BufferedImage(destCM,
                destCM.createCompatibleWritableRaster(
                        src.getWidth(), src.getHeight()),
                destCM.isAlphaPremultiplied(), null);
    }

    public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return (Point2D) srcPt.clone();
    }
}
