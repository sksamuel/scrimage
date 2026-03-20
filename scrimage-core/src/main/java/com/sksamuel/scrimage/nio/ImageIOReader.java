package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ImageIOReader implements ImageReader {

   private final List<javax.imageio.ImageReader> readers;

   public ImageIOReader() {
      readers = Collections.emptyList();
   }

   public ImageIOReader(List<javax.imageio.ImageReader> readers) {
      this.readers = readers;
   }

   private ImmutableImage tryLoad(javax.imageio.ImageReader reader, ImageInputStream iis, Rectangle rectangle) throws IOException {
      try {
         reader.setInput(iis);

         ImageReadParam params = reader.getDefaultReadParam();
         Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
         if (imageTypes.hasNext()) {
            ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
            params.setDestinationType(imageTypeSpecifier);
         }
         if (rectangle != null) {
            params.setSourceRegion(rectangle);
         }

         BufferedImage bufferedImage = reader.read(0, params);
         return ImmutableImage.wrapAwt(bufferedImage);
      } finally {
         // javax.imageio.ImageReader is a thin Java wrapper around a native decoder. When you read a JPEG,
         // the JDK loads libjavajpeg.so and allocates native structs (Huffman tables, coefficient buffers, SOF data)
         // using malloc. When you read a WebP, the luciad
         // webp-imageio plugin calls WebPEncoderOptions_createConfig which does a calloc in native code.
         // These allocations live outside the Java heap — the GC has no visibility into them.
         //
         //  The contract for javax.imageio.ImageReader is that you must call reader.dispose() when done.
         //  That method triggers the JNI dispose call which runs the corresponding free() on those native structs.
         //  Without it, the memory is permanently leaked for the lifetime of the JVM process.
         reader.dispose();
      }
   }

   @Override
   public ImmutableImage read(byte[] bytes, Rectangle rectangle) throws IOException {

      if (bytes == null)
         throw new IOException("bytes cannot be null");

      ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
      if (iis == null)
         throw new IOException("No ImageInputStream supported this image format");

      try {
         Iterator<javax.imageio.ImageReader> iter;
         if (readers.isEmpty())
            iter = ImageIO.getImageReaders(iis);
         else
            iter = readers.iterator();

         List<String> attempts = new ArrayList<>();
         while (iter.hasNext()) {
            try {
               return tryLoad(iter.next(), iis, rectangle);
            } catch (Exception e) {
               attempts.add(e.getMessage());
            }
         }

         if (attempts.isEmpty())
            throw new IOException("No javax.imageio.ImageReader supported this image format");
         else
            throw new IOException("No javax.imageio.ImageReader supported this image format; tried " + attempts.size() + " readers; errors=" + attempts);
      } finally {
         iis.close();
      }
   }

   @Override
   public String toString() {
      return "com.sksamuel.scrimage.nio.ImageIOReader (delegates to the JDK javax.imageio readers)";
   }
}
