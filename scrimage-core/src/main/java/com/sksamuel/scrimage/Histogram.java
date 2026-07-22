package com.sksamuel.scrimage;

/**
 * The distribution of channel values across all pixels of an image.
 * <p>
 * Each channel is represented as a 256-element array where index <code>i</code>
 * holds the number of pixels whose value in that channel is exactly <code>i</code>.
 * Counts are held as <code>long</code>s so that large images cannot overflow a bucket.
 */
public class Histogram {

   private final long[] red = new long[256];
   private final long[] green = new long[256];
   private final long[] blue = new long[256];
   private final long[] alpha = new long[256];
   private final long[] luminance = new long[256];

   /**
    * Builds a histogram in a single pass over every pixel of the given image.
    *
    * @param image the image to analyse
    */
   public Histogram(AwtImage image) {
      int[] argb = image.awt().getRGB(0, 0, image.width, image.height, null, 0, image.width);
      for (int p : argb) {
         int a = (p >>> 24) & 0xFF;
         int r = (p >> 16) & 0xFF;
         int g = (p >> 8) & 0xFF;
         int b = p & 0xFF;
         alpha[a]++;
         red[r]++;
         green[g]++;
         blue[b]++;
         // Rec. 709 perceived luminance, computed per pixel.
         luminance[(int) Math.round(0.2126 * r + 0.7152 * g + 0.0722 * b)]++;
      }
   }

   /**
    * @return the per-value counts for the red channel; index i holds the number of pixels with red == i.
    */
   public long[] red() {
      return red.clone();
   }

   /**
    * @return the per-value counts for the green channel; index i holds the number of pixels with green == i.
    */
   public long[] green() {
      return green.clone();
   }

   /**
    * @return the per-value counts for the blue channel; index i holds the number of pixels with blue == i.
    */
   public long[] blue() {
      return blue.clone();
   }

   /**
    * @return the per-value counts for the alpha channel; index i holds the number of pixels with alpha == i.
    */
   public long[] alpha() {
      return alpha.clone();
   }

   /**
    * Returns the distribution of perceived brightness. Each pixel contributes to a single bucket,
    * computed from its red, green and blue channels using the Rec. 709 weights
    * (0.2126R + 0.7152G + 0.0722B). The counts therefore sum to the pixel count of the image.
    *
    * @return a 256-element array of luminance counts.
    */
   public long[] luminance() {
      return luminance.clone();
   }
}
