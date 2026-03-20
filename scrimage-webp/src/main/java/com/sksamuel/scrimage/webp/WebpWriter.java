package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.PngWriter;

import java.io.IOException;
import java.io.OutputStream;

public class WebpWriter implements ImageWriter {

   public static final WebpWriter DEFAULT = new WebpWriter();
   public static final WebpWriter MAX_LOSSLESS_COMPRESSION = WebpWriter.DEFAULT.withZ(9);

   private final CWebpHandler handler = new CWebpHandler();

   private final int z;
   private final int q;
   private final int m;
   private final boolean lossless;
   private final boolean noAlpha;
   private final boolean multiThread;

   public WebpWriter() {
      z = -1;
      q = -1;
      m = -1;
      lossless = false;
      noAlpha = false;
      multiThread = false;
   }

   public WebpWriter(int z, int q, int m, boolean lossless) {
      this.z = z;
      this.q = q;
      this.m = m;
      this.lossless = lossless;
      this.noAlpha = false;
      this.multiThread = false;
   }

   public WebpWriter(int z, int q, int m, boolean lossless, boolean noAlpha) {
      this.z = z;
      this.q = q;
      this.m = m;
      this.lossless = lossless;
      this.noAlpha = noAlpha;
      this.multiThread = false;
   }

   public WebpWriter(int z, int q, int m, boolean lossless, boolean noAlpha, boolean multiThread) {
      this.z = z;
      this.q = q;
      this.m = m;
      this.lossless = lossless;
      this.noAlpha = noAlpha;
      this.multiThread = multiThread;
   }

   /**
    * Returns a new {@code WebpWriter} instance with identical properties to the current instance
    * but configured to use lossless compression.
    *
    * @return a new {@code WebpWriter} instance with lossless compression enabled.
    */
   public WebpWriter withLossless() {
      return new WebpWriter(z, q, m, true);
   }

   /**
    * Returns a new {@code WebpWriter} instance with identical properties to the current instance
    * but with alpha channel support disabled.
    *
    * @return a new {@code WebpWriter} instance with alpha channel support disabled.
    */
   public WebpWriter withoutAlpha() {
      return new WebpWriter(z, q, m, lossless, true);
   }

   /**
    * Returns a new {@code WebpWriter} instance with identical properties to the
    * current instance but configured to enable multi-threading for operations.
    *
    * @return a new {@code WebpWriter} instance with multi-threading enabled.
    */
   public WebpWriter withMultiThread() {
      return new WebpWriter(z, q, m, lossless, noAlpha, true);
   }

   /**
    * Returns a new {@code WebpWriter} instance with the same properties as the current instance
    * but configured with the specified quality parameter.
    * The quality parameter defines the level of compression for the output, ranging from 0 (maximum compression, lowest quality)
    * to 100 (least compression, highest quality).
    */
   public WebpWriter withQ(int q) {
      if (q < 0) throw new IllegalArgumentException("q must be between 0 and 100");
      if (q > 100) throw new IllegalArgumentException("q must be between 0 and 100");
      return new WebpWriter(z, q, m, lossless, noAlpha);
   }

   /**
    * Returns a new {@code WebpWriter} instance with identical properties to the current
    * instance but configured with the specified compression method.
    * The compression method affects the entropy coding and quality optimizations
    * during compression, where higher values may improve compression efficiency.
    * The value must be between 0 and 6 inclusive.
    */
   public WebpWriter withM(int m) {
      if (m < 0) throw new IllegalArgumentException("m must be between 0 and 6");
      if (m > 6) throw new IllegalArgumentException("m must be between 0 and 6");
      return new WebpWriter(z, q, m, lossless, noAlpha);
   }

   /**
    * Configures the current {@code WebpWriter} instance with the specified compression level.
    * The compression level affects the trade-off between file size and compression time.
    * Values range from 0 (fastest, least compression) to 9 (slowest, highest compression).
    * Throws an {@code IllegalArgumentException} if the value is out of range.
    */
   public WebpWriter withZ(int z) {
      if (z < 0) throw new IllegalArgumentException("z must be between 0 and 9");
      if (z > 9) throw new IllegalArgumentException("z must be between 0 and 9");
      return new WebpWriter(z, q, m, lossless, noAlpha);
   }

   @Override
   public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {
      byte[] bytes = handler.convert(image.bytes(PngWriter.NoCompression), m, q, z, lossless, noAlpha, multiThread);
      out.write(bytes);
   }
}
