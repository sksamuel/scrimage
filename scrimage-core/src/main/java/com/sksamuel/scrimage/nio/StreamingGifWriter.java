package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.DisposeMethod;
import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.ImageWriter;
import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Baseed on work by Elliot Kroo on 2009-04-25 and adapted into Java and rewritten.
 * <p>
 * This work is licensed under the Creative Commons Attribution 3.0 Unported
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * <p>
 * Typical usage will look something like:
 * {{{
 * import com.sksamuel.scrimage.nio.StreamingGifWriter
 * val stream = StreamingGifWriter()
 * stream.prepareStream("/path/to/gif.gif", BufferedImage.TYPE_INT_ARGB)
 * stream.writeFrame(image0)
 * stream.writeFrame(image1)
 * stream.writeFrame(imageN)
 * stream.finish()
 * }}}
 */
public class StreamingGifWriter extends AbstractGifWriter {

   private final Duration frameDelay;
   private final boolean infiniteLoop;
   private final boolean compressed;

   public StreamingGifWriter() {
      this.frameDelay = Duration.ofSeconds(2);
      this.infiniteLoop = true;
      this.compressed = false;
   }

   public StreamingGifWriter(Duration frameDelay, boolean infiniteLoop, boolean compressed) {
      this.frameDelay = frameDelay;
      this.infiniteLoop = infiniteLoop;
      this.compressed = compressed;
   }

   public StreamingGifWriter withFrameDelay(Duration delay) {
      return new StreamingGifWriter(delay, infiniteLoop, compressed);
   }

   public StreamingGifWriter withInfiniteLoop(boolean infiniteLoop) {
      return new StreamingGifWriter(frameDelay, infiniteLoop, compressed);
   }

   public StreamingGifWriter withCompression(boolean compressed) {
      return new StreamingGifWriter(frameDelay, infiniteLoop, compressed);
   }

   /**
    * A sequence of frames being written to a GIF. Frames may be written until {@link #close()},
    * after which {@code writeFrame} throws {@link IOException}. {@code close()} is idempotent.
    */
   public interface GifStream extends AutoCloseable {
      GifStream writeFrame(ImmutableImage image) throws IOException;

      GifStream writeFrame(ImmutableImage image, Duration delay) throws IOException;

      GifStream writeFrame(ImmutableImage image, DisposeMethod disposeMethod) throws IOException;

      GifStream writeFrame(ImmutableImage image, Duration delay, DisposeMethod disposalMethod) throws IOException;
   }

   public GifStream prepareStream(String path, int imageType) throws IOException {
      return prepareStream(Paths.get(path), imageType);
   }

   public GifStream prepareStream(Path path, int imageType) throws IOException {
      return prepareStream(path.toFile(), imageType);
   }

   public GifStream prepareStream(File file, int imageType) throws IOException {
      FileOutputStream output = new FileOutputStream(file);
      try {
         return prepareStream(output, imageType);
      } catch (IOException | RuntimeException e) {
         try {
            output.close();
         } catch (IOException suppressed) {
            e.addSuppressed(suppressed);
         }
         throw e;
      }
   }

   public GifStream prepareStream(OutputStream output, int imageType) throws IOException {

      ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
      // Until the GifStream below is constructed and returned, nothing else owns
      // the writer or the IIO stream. If any setup step throws (setFromTree can
      // raise IIOInvalidTreeException, prepareWriteSequence can raise IOException),
      // GifStream.close() is never reachable, so dispose/close them here.
      MemoryCacheImageOutputStream ios = null;
      try {
         ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

         ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
         IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

         String metaFormatName = imageMetaData.getNativeMetadataFormatName();

         IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
         populateGraphicsControlNode(root, frameDelay);
         populateCommentsNode(root);
         if (infiniteLoop)
            populateApplicationExtensions(root, infiniteLoop);

         imageMetaData.setFromTree(metaFormatName, root);

         ios = new MemoryCacheImageOutputStream(output);

         writer.setOutput(ios);
         writer.prepareWriteSequence(null);

         return buildStream(writer, ios, imageMetaData, imageWriteParam, metaFormatName, output);
      } catch (IOException | RuntimeException e) {
         try {
            writer.dispose();
         } finally {
            if (ios != null) ios.close();
         }
         throw e;
      }
   }

   private GifStream buildStream(ImageWriter writer,
                                 MemoryCacheImageOutputStream ios,
                                 IIOMetadata imageMetaData,
                                 ImageWriteParam imageWriteParam,
                                 String metaFormatName,
                                 OutputStream output) {

      return new GifStream() {

         private ImmutableImage last = null;
         private boolean closed = false;

         @Override
         public GifStream writeFrame(ImmutableImage image) throws IOException {
            return writeFrame(image, null, null);
         }

         @Override
         public GifStream writeFrame(ImmutableImage image, Duration delay) throws IOException {
            return writeFrame(image, delay, null);
         }

         @Override
         public GifStream writeFrame(ImmutableImage image, DisposeMethod disposeMethod) throws IOException {
            return writeFrame(image, null, disposeMethod);
         }

         @Override
         public GifStream writeFrame(ImmutableImage image, Duration delay, DisposeMethod disposalMethod) throws IOException {
            if (closed) {
               throw new IOException("stream is closed");
            }
            // All overloads funnel through here so that per-frame delay/dispose overrides and the
            // compressed transparency-diff always compose. Previously the override overloads wrote the
            // frame directly, which (a) leaked the overridden GraphicControlExtension attributes into
            // every later frame (the shared imageMetaData was mutated and never restored), and (b) in
            // compressed mode skipped the diff and the `last` snapshot update, so the next plain frame
            // diffed against a stale image and zeroed pixels that should have been visible.
            setGraphicControlExtensionAttributes(delay, disposalMethod);
            if (compressed && last != null) {
               // Diff whole ARGB pixels and blank the unchanged ones to fully transparent, so the
               // previous frame shows through. This used to zero raw buffer elements of the frame's
               // own type, which only works for TYPE_INT_ARGB: for byte-backed types it zeroed single
               // channel bytes (an opaque TYPE_4BYTE_ABGR pixel whose colour changed but whose alpha
               // byte matched became transparent, showing the old colour), and for types without
               // alpha a zeroed pixel is opaque black rather than transparent.
               // copy(TYPE_INT_ARGB) always yields a fresh int buffer, so the caller's image is
               // never mutated.
               ImmutableImage work = image.copy(TYPE_INT_ARGB);
               if (work.width == last.width && work.height == last.height) {
                  int[] workData = ((DataBufferInt) work.awt().getRaster().getDataBuffer()).getData();
                  int[] lastData = ((DataBufferInt) last.awt().getRaster().getDataBuffer()).getData();
                  for (int i = 0; i < workData.length; i++) {
                     if (workData[i] == lastData[i]) {
                        workData[i] = 0;
                     }
                  }
               }
               last = image.copy(TYPE_INT_ARGB);
               writer.writeToSequence(new IIOImage(work.awt(), null, imageMetaData), imageWriteParam);
            } else {
               writer.writeToSequence(new IIOImage(image.awt(), null, imageMetaData), imageWriteParam);
               if (compressed) last = image.copy(TYPE_INT_ARGB);
            }
            return this;
         }

         private void setGraphicControlExtensionAttributes(Duration delay, DisposeMethod disposeMethod)
            throws IIOInvalidTreeException {
            // Both attributes are always written, falling back to the writer's defaults when no
            // override is given. The shared imageMetaData instance carries the attributes of the
            // previously written frame, so writing only the non-null overrides would let a
            // per-frame override leak into every subsequent frame.
            IIOMetadataNode rootOverride = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
            IIOMetadataNode graphicsControlExtensionNodeOverride = getNode(rootOverride, "GraphicControlExtension");
            Duration effectiveDelay = delay == null ? frameDelay : delay;
            graphicsControlExtensionNodeOverride.setAttribute("delayTime", (effectiveDelay.toMillis() / 10L) + "");
            DisposeMethod effectiveDispose = disposeMethod == null ? DisposeMethod.NONE : disposeMethod;
            graphicsControlExtensionNodeOverride.setAttribute("disposalMethod", effectiveDispose.getDisposeMethodValue());
            imageMetaData.setFromTree(metaFormatName, rootOverride);
         }

         @Override
         public void close() throws IOException {
            // Idempotent, as AutoCloseable asks. A second close used to call endWriteSequence
            // on the disposed writer and then close the already closed ios, which surfaced as
            // a NullPointerException from MemoryCacheImageOutputStream.
            if (closed) {
               return;
            }
            closed = true;
            try {
               writer.endWriteSequence();
            } finally {
               try {
                  writer.dispose();
               } finally {
                  try {
                     ios.close();
                  } finally {
                     output.close();
                  }
               }
            }
         }
      };
   }

}
