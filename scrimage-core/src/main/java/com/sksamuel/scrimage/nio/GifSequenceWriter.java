package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Baseed on work by Elliot Kroo on 2009-04-25 and adapted rewritten.
 * <p>
 * This work is licensed under the Creative Commons Attribution 3.0 Unported
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
public class GifSequenceWriter extends AbstractGifWriter {

   private final long frameDelayMillis;
   private final boolean infiniteLoop;

   public GifSequenceWriter(long frameDelayMillis, boolean infiniteLoop) {
      this.frameDelayMillis = frameDelayMillis;
      this.infiniteLoop = infiniteLoop;
   }

   public GifSequenceWriter() {
      this(1000, true);
   }

   public GifSequenceWriter withFrameDelay(long frameDelayMillis) {
      return new GifSequenceWriter(frameDelayMillis, infiniteLoop);
   }

   public GifSequenceWriter withInfiniteLoop(boolean infiniteLoop) {
      return new GifSequenceWriter(frameDelayMillis, infiniteLoop);
   }

   public Path output(ImmutableImage[] images, Path path) throws IOException {
      return Files.write(path, bytes(images));
   }

   public Path output(ImmutableImage[] images, File file) throws IOException {
      return Files.write(file.toPath(), bytes(images));
   }

   public Path output(ImmutableImage[] images, String path) throws IOException {
      return Files.write(Paths.get(path), bytes(images));
   }

   public byte[] bytes(ImmutableImage[] images) throws IOException {

      ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
      ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

      ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(images[0].awt().getType());
      IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

      String metaFormatName = imageMetaData.getNativeMetadataFormatName();

      IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
      populateGraphicsControlNode(root, Duration.ofMillis(frameDelayMillis));
      populateCommentsNode(root);
      if (infiniteLoop)
         populateApplicationExtensions(root, infiniteLoop);

      imageMetaData.setFromTree(metaFormatName, root);

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
         try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos)) {

            writer.setOutput(output);
            writer.prepareWriteSequence(null);

            for (ImmutableImage image : images) {
               writer.writeToSequence(new IIOImage(image.awt(), null, imageMetaData), imageWriteParam);
            }

            writer.endWriteSequence();
            output.flush();
            return baos.toByteArray();
         }
      }
   }
}
