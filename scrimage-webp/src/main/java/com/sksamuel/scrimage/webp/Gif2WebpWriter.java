package com.sksamuel.scrimage.webp;

import com.sksamuel.scrimage.nio.AnimatedGif;
import com.sksamuel.scrimage.nio.AnimatedImageWriter;
import java.io.IOException;
import java.io.OutputStream;

public class Gif2WebpWriter implements AnimatedImageWriter {

   public static final Gif2WebpWriter DEFAULT = new Gif2WebpWriter();

   private final Gif2WebpHandler handler = new Gif2WebpHandler();

   private final int q;
   private final int m;
   private final boolean lossy;

   public Gif2WebpWriter() {
      q = -1;
      m = -1;
      lossy = false;
   }

   public Gif2WebpWriter(int q, int m, boolean lossy) {
      this.q = q;
      this.m = m;
      this.lossy = lossy;
   }

   public Gif2WebpWriter withLossy() {
      return new Gif2WebpWriter(q, m, true);
   }

   public Gif2WebpWriter withQ(int q) {
      if (q < 0) {
         throw new IllegalArgumentException("q must be between 0 and 100");
      }
      if (q > 100) {
         throw new IllegalArgumentException("q must be between 0 and 100");
      }
      return new Gif2WebpWriter(q, m, lossy);
   }

   public Gif2WebpWriter withM(int m) {
      if (m < 0) {
         throw new IllegalArgumentException("m must be between 0 and 6");
      }
      if (m > 6) {
         throw new IllegalArgumentException("m must be between 0 and 6");
      }
      return new Gif2WebpWriter(q, m, lossy);
   }

   @Override
   public void write(AnimatedGif gif, OutputStream out) throws IOException {
      byte[] bytes = handler.convert(gif.getBytes(), m, q, lossy);
      out.write(bytes);
   }
}
