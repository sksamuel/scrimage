package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.metadata.OrientationTools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

   public ImmutableImage fromUrl(URL url) throws IOException {
      URLConnection connection = url.openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      try (InputStream input = connection.getInputStream()) {
         return fromStream(input);
      }
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

   /**
    * Creates an {@link ImmutableImage} from a PyTorch-style tensor stored as a flat int array.
    *
    * <p>The tensor must be in CHW (channel, height, width) order with pixel values in the range
    * 0–255. Supported channel counts are 1 (grayscale), 3 (RGB), and 4 (RGBA).
    *
    * <p>The resulting image type is {@link BufferedImage#TYPE_INT_ARGB} for 4-channel tensors,
    * {@link BufferedImage#TYPE_INT_RGB} for 1- or 3-channel tensors, unless overridden via
    * {@link #type(int)}.
    *
    * @param data   flat tensor data in CHW order, values 0–255
    * @param width  image width (W dimension)
    * @param height image height (H dimension)
    * @return a new {@link ImmutableImage}
    * @throws IllegalArgumentException if the channel count is not 1, 3, or 4, or if the data
    *                                  length does not match width × height × channels
    */
   public ImmutableImage fromTorchTensor(int[] data, int width, int height) {
      int numPixels = width * height;
      if (numPixels == 0)
         throw new IllegalArgumentException("width and height must both be greater than zero");
      if (data.length % numPixels != 0)
         throw new IllegalArgumentException(
            "Data length " + data.length + " is not divisible by width × height (" + numPixels + ")");
      int channels = data.length / numPixels;
      if (channels != 1 && channels != 3 && channels != 4)
         throw new IllegalArgumentException(
            "Tensor must have 1, 3, or 4 channels but has " + channels);

      int defaultType = channels == 4 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
      int imageType = type > 0 ? type : defaultType;
      BufferedImage buffered = new BufferedImage(width, height, imageType);

      int[] argb = new int[numPixels];
      if (channels == 1) {
         for (int i = 0; i < numPixels; i++) {
            int v = clamp(data[i]);
            argb[i] = (255 << 24) | (v << 16) | (v << 8) | v;
         }
      } else if (channels == 3) {
         int gOffset = numPixels;
         int bOffset = 2 * numPixels;
         for (int i = 0; i < numPixels; i++) {
            int r = clamp(data[i]);
            int g = clamp(data[gOffset + i]);
            int b = clamp(data[bOffset + i]);
            argb[i] = (255 << 24) | (r << 16) | (g << 8) | b;
         }
      } else {
         int gOffset = numPixels;
         int bOffset = 2 * numPixels;
         int aOffset = 3 * numPixels;
         for (int i = 0; i < numPixels; i++) {
            int r = clamp(data[i]);
            int g = clamp(data[gOffset + i]);
            int b = clamp(data[bOffset + i]);
            int a = clamp(data[aOffset + i]);
            argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
         }
      }
      buffered.setRGB(0, 0, width, height, argb, 0, width);

      return ImmutableImage.wrapAwt(buffered);
   }

   /**
    * Creates an {@link ImmutableImage} from a PyTorch-style tensor stored as a flat float array.
    *
    * <p>The tensor must be in CHW (channel, height, width) order with pixel values in the range
    * 0.0–1.0. Values outside this range are clamped. Supported channel counts are 1 (grayscale),
    * 3 (RGB), and 4 (RGBA).
    *
    * <p>The resulting image type is {@link BufferedImage#TYPE_INT_ARGB} for 4-channel tensors,
    * {@link BufferedImage#TYPE_INT_RGB} for 1- or 3-channel tensors, unless overridden via
    * {@link #type(int)}.
    *
    * @param data   flat tensor data in CHW order, values 0.0–1.0
    * @param width  image width (W dimension)
    * @param height image height (H dimension)
    * @return a new {@link ImmutableImage}
    * @throws IllegalArgumentException if the channel count is not 1, 3, or 4, or if the data
    *                                  length does not match width × height × channels
    */
   public ImmutableImage fromTorchTensor(float[] data, int width, int height) {
      int[] intData = new int[data.length];
      for (int i = 0; i < data.length; i++) {
         intData[i] = Math.round(data[i] * 255f);
      }
      return fromTorchTensor(intData, width, height);
   }

   private static int clamp(int value) {
      return Math.max(0, Math.min(255, value));
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
