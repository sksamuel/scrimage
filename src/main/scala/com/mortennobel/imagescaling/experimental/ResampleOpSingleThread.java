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
package com.mortennobel.imagescaling.experimental;

import com.mortennobel.imagescaling.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.BitSet;

/**
 * Based on work from Java Image Util ( http://schmidt.devlib.org/jiu/ )
 *
 * Note that the filter method is not thread safe
 *
 * @author Morten Nobel-Joergensen
 * @author Heinz Doerr
 */
public class ResampleOpSingleThread extends AdvancedResizeOp
{
	private final int MAX_CHANNEL_VALUE = 255;

	private int nrChannels;
	private int srcWidth;
	private int srcHeight;
	private int dstWidth;
	private int dstHeight;

	private class SubSamplingData{
		private final int[] arrN; // individual - per row or per column - nr of contributions
		private final int[] arrPixel;  // 2Dim: [wid or hei][contrib]
		private final float[] arrWeight; // 2Dim: [wid or hei][contrib]
		private final int numContributors; // the primary index length for the 2Dim arrays : arrPixel and arrWeight
		private final float width;

		private SubSamplingData(int[] arrN, int[] arrPixel, float[] arrWeight, int numContributors, float width) {
			this.arrN = arrN;
			this.arrPixel = arrPixel;
			this.arrWeight = arrWeight;
			this.numContributors = numContributors;
			this.width = width;
		}
	}

	private SubSamplingData horizontalSubsamplingData;
	private SubSamplingData verticalSubsamplingData;

	private int processedItems;
	private int totalItems;

	private ResampleFilter filter = ResampleFilters.getLanczos3Filter();

	public ResampleOpSingleThread(int destWidth, int destHeight) {
		super(DimensionConstrain.createAbsolutionDimension(destWidth, destHeight));
	}

	public ResampleOpSingleThread(DimensionConstrain dimensionConstrain) {
		super(dimensionConstrain);
	}


	public ResampleFilter getFilter() {
		return filter;
	}

	public void setFilter(ResampleFilter filter) {
		this.filter = filter;
	}

	public BufferedImage doFilter(BufferedImage srcImg, BufferedImage dest, int dstWidth, int dstHeight) {
		this.dstWidth = dstWidth;
		this.dstHeight = dstHeight;
		if (srcImg.getType() == BufferedImage.TYPE_BYTE_BINARY ||
				srcImg.getType() == BufferedImage.TYPE_BYTE_INDEXED ||
				srcImg.getType() == BufferedImage.TYPE_CUSTOM)
			srcImg = ImageUtils.convert(srcImg, srcImg.getColorModel().hasAlpha() ?
					BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);

		this.nrChannels= ImageUtils.nrChannels(srcImg);
		assert nrChannels > 0;
		this.srcWidth = srcImg.getWidth();
        this.srcHeight = srcImg.getHeight();

		this.processedItems = 0;
		this.totalItems = srcHeight;

		// Pre-calculate  sub-sampling
		horizontalSubsamplingData = createSubSampling(srcWidth, dstWidth);
		verticalSubsamplingData = createSubSampling(srcHeight, dstHeight);

		//final byte[] outPixels = new byte[dstWidth*dstHeight*nrChannels];

		// Idea: Since only a small part of the buffer is used, scaling the image, we reuse the rows to save memory
		final int bufferHeight = (int)Math.ceil(verticalSubsamplingData.width)*2;
		byte[][] workPixels = new byte[srcHeight][];
		for (int i=0;i<bufferHeight && i<srcHeight;i++){
			workPixels[i] = new byte[dstWidth*nrChannels];
		}
		for (int i=bufferHeight;i<workPixels.length;i++){
			workPixels[i] = workPixels[i%bufferHeight];
		}

		final byte[][] workPixelsCopy = workPixels;


		BufferedImage out;
		if (dest!=null && dstWidth==dest.getWidth() && dstHeight==dest.getHeight()){
			out = dest;
		}else{
			out = new BufferedImage(dstWidth, dstHeight, getResultBufferedImageType(srcImg));
		}

		scale(srcImg, workPixelsCopy, out);


		return out;
    }

	private SubSamplingData createSubSampling(int srcSize, int dstSize) {
		float scale = (float) dstSize / (float)srcSize;
		int[] arrN= new int[dstSize];
		int numContributors;
		float[] arrWeight;
		int[] arrPixel;

		final float fwidth= filter.getSamplingRadius();
		final float width;
		if (scale < 1.0f) {
			width= fwidth / scale;
			numContributors= (int)(width * 2.0f + 2); // Heinz: added 1 to be save with the ceilling
			arrWeight= new float[dstSize * numContributors];
			arrPixel= new int[dstSize * numContributors];

			final float fNormFac= (float)(1f / (Math.ceil(width) / fwidth));
			//
			for (int i= 0; i < dstSize; i++) {
				final int subindex= i * numContributors;
				float center= i / scale;
				int left= (int)Math.floor(center - width);
				int right= (int)Math.ceil(center + width);

				assert right-left<=numContributors;

				for (int j= left; j <= right; j++) {
					float weight;
					weight= filter.apply((center - j) * fNormFac);

					if (weight == 0.0f) {
						continue;
					}
					int n;
					if (j < 0) {
						n= -j;
					} else if (j >= srcSize) {
						n= srcSize - j + srcSize - 1;
					} else {
						n= j;
					}
					int k= arrN[i];
					arrN[i]++;
					if (n < 0 || n >= srcSize) {
						weight= 0.0f;// Flag that cell should not be used
					}
					arrPixel[subindex +k]= n;
					arrWeight[subindex + k]= weight;
				}
				// normalize the filter's weight's so the sum equals to 1.0, very important for avoiding box type of artifacts
				final int max= arrN[i];
				float tot= 0;
				for (int k= 0; k < max; k++)
					tot+= arrWeight[subindex + k];
				if (tot != 0f) { // 0 should never happen except bug in filter
					for (int k= 0; k < max; k++)
						arrWeight[subindex + k]/= tot;
				}
			}
		} else
			// super-sampling
			// Scales from smaller to bigger height
		{
			width = fwidth;
			numContributors= (int)(fwidth * 2.0f + 1);
			arrWeight= new float[dstSize * numContributors];
			arrPixel= new int[dstSize * numContributors];
			//
			for (int i= 0; i < dstSize; i++) {
				final int subindex= i * numContributors;
				float center= i / scale;
				int left= (int)Math.floor(center - fwidth);
				int right= (int)Math.ceil(center + fwidth);

				for (int j= left; j <= right; j++) {
					float weight= filter.apply(center - j);
					if (weight == 0.0f) {
						continue;
					}
					int n;
					if (j < 0) {
						n= -j;
					} else if (j >= srcSize) {
						n= srcSize - j + srcSize - 1;
					} else {
						n= j;
					}
					int k= arrN[i];
					arrN[i]++;
					if (n < 0 || n >= srcSize) {
						weight= 0.0f;// Flag that cell should not be used
					}
					arrPixel[subindex +k]= n;
					arrWeight[subindex + k]= weight;
				}
				// normalize the filter's weight's so the sum equals to 1.0, very important for avoiding box type of artifacts
				final int max= arrN[i];
				float tot= 0;
				for (int k= 0; k < max; k++)
					tot+= arrWeight[subindex + k];
				assert tot!=0:"should never happen except bug in filter";
				if (tot != 0f) {
					for (int k= 0; k < max; k++)
						arrWeight[subindex + k]/= tot;
				}
			}
		}
		return new SubSamplingData(arrN, arrPixel, arrWeight, numContributors, width);
	}

	/**
	 * Pseudocode:
	 * for each for in destination image
	 *   check that all dependent rows in source image is scaled horizontally and stored in work pixels
	 *     if not then scale missing rows horizontal and store them in work pixels
	 *   Scale the destination row vertically and store the result in out pixels
	 *
	 * It may seem counter intuitive that the vertical scale is done for each row. The simple scaling algorithm would
	 * first scale the image horizontal (a row at a time) into the temp image, and then scale the temp image vertically
	 * (a column at a time) into the final image. The disadvantage of the simple approach is that you need a large
	 * temporary image.
	 *
	 * I have avoided this by doing the vertically scale a row at a time. Scaling a single row vertically, needs the
	 * horizontally scaled rows that the scaling depends on. These dependencies will be lazy initialized.
	 * Since we know the height of the 'window' we work on (the maximum number of source rows needed to calculate a dest
	 * row), the work pixels only needs to have the same height.
	 *
	 * Instead of creating a temporary image with a height different from the source image's height, I created a double
	 * array where inner array is repeated (so if the window height is 3 the first and the forth row is the same instance).
	 * This keeps algorithm a bit simpler (the alternative would be to maintain a mapping between) 
	 *
	 * @param srcImg source image
	 * @param workPixels temporary image
	 * @param outImage result image
	 */
	private void scale(BufferedImage srcImg, byte[][] workPixels, BufferedImage outImage) {
		final int[] tempPixels = new int[srcWidth];   // Used if we work on int based bitmaps, later used to keep channel values
		final byte[] srcPixels = new byte[srcWidth*nrChannels]; // create reusable row to minimize memory overhead

		final byte[] dstPixels = new byte[dstWidth*nrChannels];

		final BitSet isRowInitialized = new BitSet(srcHeight);

		for (int dstY = dstHeight-1; dstY >= 0; dstY--)
        {
			// vertical scaling
			final int yTimesNumContributors = dstY * verticalSubsamplingData.numContributors;
			final int max= verticalSubsamplingData.arrN[dstY];

			// check that the horizontal rows are scaled horizontally
			{
				int index= yTimesNumContributors;
				for (int j= max-1; j >=0 ; j--) {
					int valueLocation = verticalSubsamplingData.arrPixel[index];
					index++;
					if (!isRowInitialized.get(valueLocation)){
						// do horizontal scaling
						isRowInitialized.set(valueLocation);

						// scale row horizontally
						ImageUtils.getPixelsBGR(srcImg, valueLocation, srcWidth, srcPixels, tempPixels);

						for (int channel = nrChannels-1;channel>=0 ; channel--)
						{
							// reuse tempPixels as sample value
							getSamplesHorizontal(srcPixels,channel,tempPixels);

							for (int i = dstWidth-1;i>=0 ; i--)
							{
								int sampleLocation = i*nrChannels;
								final int horizontalMax = horizontalSubsamplingData.arrN[i];

								float sample= 0.0f;
								int horizontalIndex= i * horizontalSubsamplingData.numContributors;
								for (int jj= horizontalMax-1; jj >= 0; jj--) {

									sample += tempPixels[horizontalSubsamplingData.arrPixel[horizontalIndex]] * horizontalSubsamplingData.arrWeight[horizontalIndex];
									horizontalIndex++;
								}

								putSample(workPixels[valueLocation], channel, (int)sample, sampleLocation);
							}
						}
					}
				}
			}

			for (int x = 0; x < dstWidth; x++)
			{
				final int xLocation = x*nrChannels;
				final int sampleLocation = x*nrChannels;

				for (int channel = nrChannels-1; channel >=0 ; channel--)
				{
					float sample = 0.0f;
					int index= yTimesNumContributors;
					for (int j= max-1; j >=0 ; j--) {
						int valueLocation = verticalSubsamplingData.arrPixel[index];
						sample+= (workPixels[valueLocation][xLocation+channel]&0xff) * verticalSubsamplingData.arrWeight[index];

						index++;
					}

					putSample(dstPixels, channel, sample, sampleLocation);
				}
			}


			ImageUtils.setBGRPixels(dstPixels, outImage, 0, dstY, dstWidth, 1);

			setProgress(processedItems++, totalItems);
		}
	}


    private void putSample(byte[] image, int channel, float sample, int location) {
		int result = (int) sample;
		if (sample<0){
			result = 0;
		}
		else if (result > MAX_CHANNEL_VALUE) {
			result= MAX_CHANNEL_VALUE;
		}
		image[location+channel] = (byte)result;
    }

	private void getSamplesHorizontal(byte[] src, int channel, int[] dest){
		for (int xDest=0, x=channel;x<src.length;x+=nrChannels,xDest++){
			dest[xDest] = src[x]&0xff;
		}
	}

	private void setProgress(int zeroBasedIndex, int totalItems){
        fireProgressChanged(zeroBasedIndex/(float)totalItems);
    }

	protected int getResultBufferedImageType(BufferedImage srcImg) {
		return nrChannels == 3 ? BufferedImage.TYPE_3BYTE_BGR :
							(nrChannels == 4 ? BufferedImage.TYPE_4BYTE_ABGR :
								(srcImg.getSampleModel().getDataType() == DataBuffer.TYPE_USHORT ?
										BufferedImage.TYPE_USHORT_GRAY : BufferedImage.TYPE_BYTE_GRAY));
	}
}

