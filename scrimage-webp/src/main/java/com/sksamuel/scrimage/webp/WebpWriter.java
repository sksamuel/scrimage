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

   public WebpWriter withLossless() {
      return new WebpWriter(z, q, m, true);
   }

   public WebpWriter withoutAlpha() {
      return new WebpWriter(z, q, m, lossless, true);
   }

   public WebpWriter withMultiThread() {
      return new WebpWriter(z, q, m, lossless, noAlpha, true);
   }

   public WebpWriter withQ(int q) {
      if (q < 0) throw new IllegalArgumentException("q must be between 0 and 100");
      if (q > 100) throw new IllegalArgumentException("q must be between 0 and 100");
      return new WebpWriter(z, q, m, lossless, noAlpha);
   }

   public WebpWriter withM(int m) {
      if (m < 0) throw new IllegalArgumentException("m must be between 0 and 6");
      if (m > 6) throw new IllegalArgumentException("m must be between 0 and 6");
      return new WebpWriter(z, q, m, lossless, noAlpha);
   }

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
