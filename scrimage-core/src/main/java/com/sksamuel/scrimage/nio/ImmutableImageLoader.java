package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.metadata.OrientationTools;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ImmutableImageLoader {

   private boolean reorientate = true;
   private Rectangle rectangle = null;
   private int type = 0;
   private boolean metadata = true;

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

   public ImmutableImage fromBytes(byte[] bytes) throws IOException {
      return load(new ByteArrayImageSource(bytes));
   }

   public ImmutableImage fromFile(File file) throws IOException {
      return load(new FileImageSource(file));
   }

   public ImmutableImage fromFile(String file) throws IOException {
      return fromFile(new File(file));
   }

   public ImmutableImage fromPath(Path path) throws IOException {
      return load(new FileImageSource(path));
   }

   public ImmutableImage fromResource(String resource) throws IOException {
      return fromStream(getClass().getResourceAsStream(resource));
   }

   public ImmutableImage fromStream(InputStream stream) throws IOException {
      return load(new InputStreamImageSource(stream));
   }

   public ImmutableImage load(ImageSource source) throws IOException {

      ImageSource cached = new CachedImageSource(source);
      ImmutableImage image = ImageReaders.read(cached, rectangle);

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
