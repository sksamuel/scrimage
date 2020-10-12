package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TiffWriter extends TwelveMonkeysWriter {

   // see https://download.java.net/media/jai-imageio/javadoc/1.1/com/sun/media/imageio/plugins/tiff/TIFFImageWriteParam.html
   private final String compressionType;

   public TiffWriter() {
      this.compressionType = null;
   }

   public TiffWriter(String compressionType) {
      this.compressionType = compressionType;
   }

   @Override
   public String format() {
      return "tiff";
   }

   /**
    * Sets the compression type.
    * Must be one of https://download.java.net/media/jai-imageio/javadoc/1.1/com/sun/media/imageio/plugins/tiff/TIFFImageWriteParam.html
    */
   public TiffWriter withCompressionType(String compressionType) {
      return new TiffWriter(compressionType);
   }

   public String getCompressionType() {
      return compressionType;
   }

   @Override
   public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {
      javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName(format()).next();
      ImageOutputStream ios = ImageIO.createImageOutputStream(out);
      ImageWriteParam params = writer.getDefaultWriteParam();

      if (compressionType != null) {
         params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
         params.setCompressionType(compressionType);
      }

      writer.setOutput(ios);
      writer.write(null, new IIOImage(image.awt(), null, null), params);
      ios.close();
      writer.dispose();
   }
}

