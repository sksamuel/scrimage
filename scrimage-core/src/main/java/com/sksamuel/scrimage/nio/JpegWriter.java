package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;

public class JpegWriter implements ImageWriter {

   // This compression mode cannot be used with JPEG. Instead, use CompressionFromMetaData
   @Deprecated
   public static JpegWriter NoCompression = new JpegWriter(100, false);

   public static JpegWriter CompressionFromMetaData = new JpegWriter(-1, false);
   public static JpegWriter Default = new JpegWriter(80, false);

   /**
    * @param compression quality between 0 (full lossy compression) and 100 (best quality),
    *                    or -1 to copy the quality from the image metadata
    * @throws IllegalArgumentException if compression is outside -1..100
    */
   public static JpegWriter compression(int compression) {
      return new JpegWriter(compression, false);
   }

   private final int compression;
   private final boolean progressive;

   /**
    * @param compression quality between 0 (full lossy compression) and 100 (best quality),
    *                    or -1 to copy the quality from the image metadata
    * @param progressive whether to write a progressive JPEG
    * @throws IllegalArgumentException if compression is outside -1..100
    */
   public JpegWriter(int compression, boolean progressive) {
      // Validate up front, as PngWriter does. An out of range value used to fall into the
      // MODE_COPY_FROM_METADATA branch in write() and silently encode at the JDK default
      // quality rather than the quality the caller asked for.
      if (compression < -1 || compression > 100) {
         throw new IllegalArgumentException(
            "JPEG compression must be between 0 and 100 (or -1 to copy from metadata), got " + compression);
      }
      this.compression = compression;
      this.progressive = progressive;
   }

   public JpegWriter() {
      this(80, false);
   }

   /**
    * @param compression quality between 0 (full lossy compression) and 100 (best quality),
    *                    or -1 to copy the quality from the image metadata
    * @throws IllegalArgumentException if compression is outside -1..100
    */
   public JpegWriter withCompression(int compression) {
      return new JpegWriter(compression, progressive);
   }

   public JpegWriter withProgressive(boolean progressive) {
      return new JpegWriter(compression, progressive);
   }

   @Override
   public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {

      javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
      // dispose() in a finally that spans every use of the writer. Acquiring it
      // here but only disposing inside the write try-block leaked the native
      // JPEG writer if anything in between threw — e.g. allocating the no-alpha
      // BufferedImage for a very large image, or setCompressionQuality.
      try {
      ImageWriteParam params = writer.getDefaultWriteParam();

      // compression is a quality knob in [0, 100]. The earlier code
      // treated `compression == 100` as MODE_DISABLED — invalid for
      // JPEG (JPEG is always compressed) and rejected at write time
      // by the JDK writer. Treat any non-negative compression in
      // [0, 100] as explicit quality, including 100 → quality=1.0
      // (the best the codec offers). Negative values (the existing
      // CompressionFromMetaData = -1 sentinel) take the
      // COPY_FROM_METADATA path.
      if (compression >= 0 && compression <= 100) {
         params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
         params.setCompressionQuality(compression / 100f);
      } else {
         params.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
      }

      if (params.canWriteProgressive()) {
         if (progressive) {
            params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
         } else {
            params.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
         }
      }

      // in openjdk, awt cannot write out jpegs that have a transparency bit, even if that is set to 255.
      // see http://stackoverflow.com/questions/464825/converting-transparent-gif-png-to-jpeg-using-java
      // so have to convert to a non alpha type
      BufferedImage noAlpha;
      if (image.awt().getColorModel().hasAlpha()) {
         noAlpha = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = noAlpha.createGraphics();
         try {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, image.width, image.height);
            g2.drawImage(image.awt(), 0, 0, null);
         } finally {
            g2.dispose();
         }
      } else if (hasWideSamples(image.awt())) {
         // The JDK JPEG writer only accepts samples of at most 8 bits, so the 16-bit images the
         // PNG reader returns for 16-bit PNGs (TYPE_USHORT_GRAY, or TYPE_CUSTOM for RGB) failed
         // with "Illegal band size". Reduce them to 8 bits per sample first.
         noAlpha = toEightBit(image.awt());
      } else {
         noAlpha = image.awt();
      }

      try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out)) {
         writer.setOutput(output);
         writer.write(null, new IIOImage(noAlpha, null, null), params);
      }

      } finally {
         writer.dispose();
      }
   }

   private static boolean hasWideSamples(BufferedImage image) {
      for (int size : image.getColorModel().getComponentSize()) {
         if (size > 8) return true;
      }
      return false;
   }

   private static BufferedImage toEightBit(BufferedImage image) {
      int w = image.getWidth();
      int h = image.getHeight();
      Raster src = image.getRaster();
      if (image.getColorModel().getNumComponents() == 1 && src.getNumBands() == 1) {
         // Single band grey: keep a greyscale JPEG and the stored sample values by taking the
         // high byte of each sample. Going through an RGB image would apply the JDK's linear
         // grey conversion and brighten the image.
         int bits = image.getColorModel().getComponentSize(0);
         BufferedImage grey = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
         WritableRaster dst = grey.getRaster();
         int[] row = new int[w];
         for (int y = 0; y < h; y++) {
            src.getSamples(0, y, w, 1, 0, row);
            for (int x = 0; x < w; x++) row[x] = row[x] >>> (bits - 8);
            dst.setSamples(0, y, w, 1, 0, row);
         }
         return grey;
      }
      BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = rgb.createGraphics();
      try {
         g2.drawImage(image, 0, 0, null);
      } finally {
         g2.dispose();
      }
      return rgb;
   }
}
