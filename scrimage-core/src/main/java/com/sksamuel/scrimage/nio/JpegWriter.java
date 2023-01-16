package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class JpegWriter implements ImageWriter {

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
      ImageWriteParam params = writer.getDefaultWriteParam();

      if (compression == 100) {
         params.setCompressionMode(ImageWriteParam.MODE_DISABLED);
      } else if (compression > -1 && compression < 100) {
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
         noAlpha = image.toImmutableImage().removeTransparency(java.awt.Color.WHITE).toNewBufferedImage(BufferedImage.TYPE_INT_RGB);
      } else {
         noAlpha = image.awt();
      }

      MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out);
      writer.setOutput(output);
      writer.write(null, new IIOImage(noAlpha, null, null), params);
      output.close();
      writer.dispose();
//        IOUtils.closeQuietly(out);
   }
}
