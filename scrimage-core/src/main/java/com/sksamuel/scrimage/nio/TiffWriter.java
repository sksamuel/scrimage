/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

public class TiffWriter implements ImageWriter {

   public static TiffWriter Default = new TiffWriter(null);

   private final String compressionType;

   public TiffWriter(String compressionType) {
      this.compressionType = compressionType;
   }

   /**
    * Sets the TIFF compression type.
    * Must be one of the supported compressionTypes.
    */
   public TiffWriter withCompressionType(String compressionType) {
      return new TiffWriter(compressionType);
   }

   @Override
   public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {

      javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("tiff").next();
      ImageWriteParam params = writer.getDefaultWriteParam();

      if (compressionType != null) {
         params.setCompressionMode(MODE_EXPLICIT);
         params.setCompressionType(compressionType);
      }

      try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out)) {
         writer.setOutput(output);
         writer.write(null, new IIOImage(image.awt(), null, null), params);
         writer.dispose();
      }
   }
}
