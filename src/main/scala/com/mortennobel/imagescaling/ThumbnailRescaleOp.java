package com.mortennobel.imagescaling;


import java.awt.image.BufferedImage;

/**
 * The idea of this class is to provide fast (and inaccurate) rescaling method
 * suitable for creating thumbnails.
 *
 * Note that the algorithm assumes that the source image is significant larger
 * than the destination image
 */
public class ThumbnailRescaleOp extends AdvancedResizeOp {
	public static enum Sampling {
		S_1SAMPLE(new float[][]{{0.5f,0.5f}}),
		S_2X2_RGSS(new float[][]{
				{0.6f,0.2f},
				{0.2f,0.4f},
				{0.8f,0.6f},
				{0.4f,0.8f},
		}),
		S_8ROCKS(new float[][]{
				{0/6f,2/6f},
				{2/6f,1/6f},
				{4/6f,0/6f},
				{5/6f,2/6f},
				{6/6f,4/6f},
				{4/6f,5/6f},
				{2/6f,6/6f},
				{1/6f,4/6f},
		})
		;
		final float[][] points;
		final int rightshift;

		Sampling(float[][] points) {
			this.points = points;
			rightshift = Integer.numberOfTrailingZeros(points.length);
		}


	}

	private Sampling sampling = Sampling.S_8ROCKS;

	public ThumbnailRescaleOp(int destWidth, int destHeight) {
		this(DimensionConstrain.createAbsolutionDimension(destWidth, destHeight));
	}

	public ThumbnailRescaleOp(DimensionConstrain dimensionConstrain) {
		super(dimensionConstrain);
	}

	protected BufferedImage doFilter(BufferedImage src, BufferedImage dest, int dstWidth, int dstHeight) {
		int numberOfChannels = ImageUtils.nrChannels(src);
		BufferedImage out;
		if (dest!=null && dstWidth==dest.getWidth() && dstHeight==dest.getHeight()){
			out = dest;
		}else{

			out = new BufferedImage(dstWidth, dstHeight, numberOfChannels==4?BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		}

		float scaleX = src.getWidth()/(float)dstWidth;
		float scaleY = src.getHeight()/(float)dstHeight;

		float[][] scaledSampling = new float[sampling.points.length][2];
		for (int i=0;i<sampling.points.length;i++){
			float[] point=sampling.points[i];
			final float ROUNDING_ERROR_MARGIN = 0.0001f;
			scaledSampling[i][0] = point[0]*scaleX+ROUNDING_ERROR_MARGIN;
			scaledSampling[i][1] = point[1]*scaleY+ROUNDING_ERROR_MARGIN;
		}
		int maxSrcX = src.getWidth()-1;
		int maxSrcY = src.getHeight()-1;
		float srcY = 0;
		for (int dstY=0;dstY<dstHeight;dstY++,srcY+=scaleY){
			float srcX = 0;
			for (int dstX=0;dstX<dstWidth;dstX++,srcX+=scaleX){
				int r = 0, g = 0, b = 0, a = 0;
				for (float[] point:scaledSampling){
					int x = (int) (srcX+point[0]);
					int y = (int) (srcY+point[1]);
					x = Math.max(0,Math.min(x, maxSrcX));
					y = Math.max(0,Math.min(y, maxSrcY));

					int rgb = src.getRGB(x,y);
					b += rgb&0xff;
					rgb = rgb>>>8;
					g += rgb&0xff;
					rgb = rgb>>>8;
					r += rgb&0xff;
					rgb = rgb>>>8;
					a += rgb&0xff;
				}
				r = r>>sampling.rightshift;
				g = g>>sampling.rightshift;
				b = b>>sampling.rightshift;
				a = a>>sampling.rightshift;

				int rgb = (a<<24)+(r<<16)+(g<<8)+b;
				out.setRGB(dstX, dstY, rgb);
			}
		}
		return out;
	}

	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}
}
