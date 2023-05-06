package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.metadata.OrientationTools;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ImmutableImageLoader {

   private boolean reorientate = true;
   private Rectangle rectangle = null;
   private int type = 0;
   private boolean metadata = true;
   private ClassLoader classloader = null;
   private List<ImageReader> readers = Collections.emptyList();

   public static ImmutableImageLoader create() {
      return new ImmutableImageLoader();
   }

   /**
    * Set to true to reorientate the image if applicable. Requires metadata to be enabled.
    * Default true.
    */
   public ImmutableImageLoader detectOrientation(boolean reorientate) {
      this.reorientate = reorientate;
      return this;
   }

   /**
    * Set to true to load metadata from the file.
    * Set to false to skip metadata load.
    * Default true.
    */
   public ImmutableImageLoader detectMetadata(boolean metadata) {
      this.metadata = metadata;
      return this;
   }

   /**
    * Specifies a region of the image to be loaded. Specifying a region here, rather than resizing
    * the canvas after load can result in a performance gain under certain loaders.
    */
   public ImmutableImageLoader sourceRegion(Rectangle rectangle) {
      this.rectangle = rectangle;
      return this;
   }

   /**
    * Set the BufferedImage type for the backing image. If the specified type is different
    * from the type detected on load, this can result in an image copy operation.
    * Default is to use the type in the underlying format.
    */
   public ImmutableImageLoader type(int type) {
      this.type = type;
      return this;
   }

   /**
    * Specifies the ImageReader's to use when trying to decode an image.
    * If not specified, then Scrimage will default to detecting the image readers on the classpath,
    * through the Java Service Loader API.
    */
   public ImmutableImageLoader withImageReaders(List<ImageReader> readers) {
      this.readers = readers;
      return this;
   }

   /**
    * Specifies to use [javax.image.ImageReader] implementations only.
    * The javax.image.ImageReader to use will be detected by the JDK based on the stream contents.
    */
   public ImmutableImageLoader withJavaxImageReaders() {
      this.readers = Collections.singletonList(new ImageIOReader());
      return this;
   }

   /**
    * Specifies to use the given [javax.image.ImageReader] implementations only.
    */
   public ImmutableImageLoader withJavaxImageReaders(List<javax.imageio.ImageReader> readers) {
      this.readers = Collections.singletonList(new ImageIOReader(readers));
      return this;
   }

   public ImmutableImageLoader withClassLoader(ClassLoader classloader) {
      this.classloader = classloader;
      return this;
   }

   public ImmutableImage fromBytes(byte[] bytes) throws IOException {
      return load(new ByteArrayImageSource(bytes));
   }

   public ImmutableImage fromFile(File file) throws IOException {
      if (!file.exists()) throw new FileNotFoundException(file.toString());
      return load(new FileImageSource(file));
   }

   public ImmutableImage fromFile(String file) throws IOException {
      return fromFile(new File(file));
   }

   public ImmutableImage fromPath(Path path) throws IOException {
      return fromFile(path.toFile());
   }

   public ImmutableImage fromResource(String resource) throws IOException {
      InputStream in = getClass().getResourceAsStream(resource);
      if (in == null)
         throw new IOException("Could not locate resource: " + resource);
      return fromStream(in);
   }

   /**
    * Configures this loader to read from the provided input stream.
    * The stream should be closed by the caller after this method has returned.
    */
   public ImmutableImage fromStream(InputStream in) throws IOException {
      if (in == null)
         throw new IOException("Input stream is null");
      return load(new InputStreamImageSource(in));
   }

   public ImmutableImage load(ImageSource source) throws IOException {

      ImageSource cached = new CachedImageSource(source);
      ImmutableImage image;
      if (readers.isEmpty())
         image = ImageReaders.read(cached, rectangle, classloader);
      else
         image = ImageReaders.read(cached, rectangle, readers);

      if (type > 0 && type != image.getType()) {
         image = image.copy(type);
      }

      if (metadata) {
         ImageMetadata metadata = ImageMetadata.load(cached);
         image = image.associateMetadata(metadata);
      }

      if (reorientate) {
         image = OrientationTools.reorient(image, image.getMetadata());
      }

      return image;
   }
}
