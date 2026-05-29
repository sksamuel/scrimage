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
import java.io.IOException;
import java.io.OutputStream;

public class JpegWriter implements ImageWriter {

   // This compression mode cannot be used with JPEG. Instead, use CompressionFromMetaData
   @Deprecated
   public static JpegWriter NoCompression = new JpegWriter(100, false);

   public static JpegWriter CompressionFromMetaData = new JpegWriter(-1, false);
   public static JpegWriter Default = new JpegWriter(80, false);

   public static JpegWriter compression(int compression) {
      return new JpegWriter(compression, false);
   }

   private final int compression;
   private final boolean progressive;

   public JpegWriter(int compression, boolean progressive) {
      this.compression = compression;
      this.progressive = progressive;
   }

   public JpegWriter() {
      this(80, false);
   }

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
}
