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
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PngWriter implements ImageWriter {

   public static final PngWriter MaxCompression = new PngWriter(9);
   public static final PngWriter MinCompression = new PngWriter(1);
   public static final PngWriter NoCompression = new PngWriter(0);

   private final int compressionLevel;

   public PngWriter() {
      this.compressionLevel = 9;
   }

   public PngWriter(int compressionLevel) {
      this.compressionLevel = compressionLevel;
   }

   // require(compressionLevel >= 0 && compressionLevel < 10, "Compression level must be between 0 (none) and 9 (max)")

   public PngWriter withMaxCompression() {
      return MaxCompression;
   }

   public PngWriter withMinCompression() {
      return MinCompression;
   }

   public PngWriter withCompression(int compression) {
      return new PngWriter(compression);
   }

   @Override
   public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {

      ImageTypeSpecifier type = ImageTypeSpecifier.createFromBufferedImageType(image.getType());
      javax.imageio.ImageWriter writer = ImageIO.getImageWriters(type, "png").next();
      ImageWriteParam param = writer.getDefaultWriteParam();

      if (param.canWriteCompressed()) {
         switch (compressionLevel) {
            case 9: // max
               param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
               param.setCompressionQuality(0.0f);
               break;
            case 1: // min
               param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
               param.setCompressionQuality(1.0f);
               break;
            case 0: // none
               param.setCompressionMode(ImageWriteParam.MODE_DISABLED);
            default:
               param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
               param.setCompressionQuality(compressionLevel / 10f);
         }
      }

      ImageOutputStream ios = ImageIO.createImageOutputStream(out);
      writer.setOutput(ios);
      writer.write(null, new IIOImage(image.awt(), null, null), param);
      writer.dispose();
      ios.close();
   }
}
