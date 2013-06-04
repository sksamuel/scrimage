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
 *
 */
package com.mortennobel.imagescaling;

import java.awt.*;

/**
 * This class let you create dimension constrains based on a actual image.
 *
 * Class may be subclassed to create user defined behavior. To do this you need to overwrite
 * the method getDimension(Dimension).
 */
public class DimensionConstrain {
	protected DimensionConstrain ()
	{
	}

	/**
	 * Will always return a dimension with positive width and height;
	 * @param dimension of the unscaled image
	 * @return the dimension of the scaled image
	 */
	public Dimension getDimension(Dimension dimension){
		return dimension;
	}

	/**
	 * Used when the destination size is fixed. This may not keep the image aspect radio
	 * @param width destination dimension width
	 * @param height destination dimension height
	 * @return  destination dimension (width x height)
	 */
	public static DimensionConstrain createAbsolutionDimension(final int width, final int height){
		assert width>0 && height>0:"Dimension must be a positive integer";
		return new DimensionConstrain(){
			public Dimension getDimension(Dimension dimension) {
				return new Dimension(width, height);
			}
		};
	}

	/**
	 * Used when the destination size is relative to the source. This keeps the image aspect radio
	 * @param fraction resize fraction (must be a positive number)
	 * @return the new dimension (the input dimension times the fraction)
	 */
	public static DimensionConstrain createRelativeDimension(final float fraction){
		return createRelativeDimension(fraction,fraction);
	}

	/**
	 * Used when the destination size is relative to the source. This keeps the image aspect radio if fractionWidth
     * equals fractionHeight
	 * @param
	 * @return
	 */
	public static DimensionConstrain createRelativeDimension(final float fractionWidth, final float fractionHeight){
		assert fractionHeight>0 && fractionWidth>0:"Fractions must be larger than 0.0";
		return new DimensionConstrain(){
			public Dimension getDimension(Dimension dimension) {
				int width = Math.max(1,Math.round(fractionWidth*dimension.width));
				int height = Math.max(1,Math.round(fractionHeight*dimension.height));
				return new Dimension(width, height);
			}
		};
	}

	/**
	 * Forces the image to keep radio and be keeped within the width and height
	 * @param width
	 * @param height
	 * @return
	 */
	public static DimensionConstrain createMaxDimension(int width, int height){
		return createMaxDimension(width, height,false);
	}

	/**
	 * Forces the image to keep radio and be keeped within the width and height.
	 * @param width
	 * @param height
	 * @param neverEnlargeImage if true only a downscale will occour
	 * @return
	 */
	public static DimensionConstrain createMaxDimension(final int width, final int height, final boolean neverEnlargeImage){
		assert width >0 && height > 0 : "Dimension must be larger that 0";
		final double scaleFactor = width/(double)height;
		return new DimensionConstrain(){
			public Dimension getDimension(Dimension dimension) {
				double srcScaleFactor = dimension.width/(double)dimension.height;
				double scale;
				if (srcScaleFactor>scaleFactor){
					scale = width/(double)dimension.width;
				}
				else{
					scale = height/(double)dimension.height;
				}
				if (neverEnlargeImage){
					scale = Math.min(scale,1);
				}
				int dstWidth = (int)Math.round (dimension.width*scale);
				int dstHeight = (int) Math.round(dimension.height*scale);
				return new Dimension(dstWidth, dstHeight);
			}
		};
	}

	/**
	 * Forces the image to keep radio and be keeped within the width and height. Width and height are defined
	 * (length1 x length2) or (length2 x length1).
	 *
	 * This is usefull is the scaling allow a certain format (such as 16x9") but allow the dimension to be rotated 90
	 * degrees (so also 9x16" is allowed).
	 *
	 * @param length1
	 * @param length2
	 * @return
	 */
	public static DimensionConstrain createMaxDimensionNoOrientation(int length1, int length2){
		return createMaxDimensionNoOrientation(length1, length2,false);
	}

	/**
	 * Forces the image to keep radio and be keeped within the width and height. Width and height are defined
	 * (length1 x length2) or (length2 x length1).
	 *
	 * This is usefull is the scaling allow a certain format (such as 16x9") but allow the dimension to be rotated 90
	 * degrees (so also 9x16" is allowed).
	 *
	 * @param length1
	 * @param length2
	 * @param neverEnlargeImage if true only a downscale will occour
	 * @return
	 */
	public static DimensionConstrain createMaxDimensionNoOrientation(final int length1, final int length2, final boolean neverEnlargeImage){
		assert length1 >0 && length2 > 0 : "Dimension must be larger that 0";
		final double scaleFactor = length1/(double)length2;
		return new DimensionConstrain(){
			public Dimension getDimension(Dimension dimension) {
				double srcScaleFactor = dimension.width/(double)dimension.height;
				int width;
				int height;
				// swap length1 and length2
				if (srcScaleFactor>scaleFactor){
					width = length1;
					height = length2;
				}
				else{
					width = length2;
					height = length1;
				}


				final double scaleFactor = width/(double)height;
				double scale;
				if (srcScaleFactor>scaleFactor){
					scale = width/(double)dimension.width;
				}
				else{
					scale = height/(double)dimension.height;
				}
				if (neverEnlargeImage){
					scale = Math.min(scale,1);
				}
				int dstWidth = (int)Math.round (dimension.width*scale);
				int dstHeight = (int) Math.round(dimension.height*scale);
				return new Dimension(dstWidth, dstHeight);
			}
		};
	}


}
