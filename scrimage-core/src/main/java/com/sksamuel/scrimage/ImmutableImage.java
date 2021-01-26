/*
   Copyright 2013-2014 Stephen K Samuel

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

package com.sksamuel.scrimage;

import com.sksamuel.scrimage.angles.Degrees;
import com.sksamuel.scrimage.angles.Radians;
import com.sksamuel.scrimage.canvas.Canvas;
import com.sksamuel.scrimage.canvas.drawables.FilledRect;
import com.sksamuel.scrimage.canvas.painters.LinearGradient;
import com.sksamuel.scrimage.canvas.painters.Painter;
import com.sksamuel.scrimage.color.Colors;
import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.composite.Composite;
import com.sksamuel.scrimage.filter.Filter;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.metadata.OrientationTools;
import com.sksamuel.scrimage.nio.ByteArrayImageSource;
import com.sksamuel.scrimage.nio.ImageReaders;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.ImmutableImageLoader;
import com.sksamuel.scrimage.nio.WriteContext;
import com.sksamuel.scrimage.pixels.Pixel;
import com.sksamuel.scrimage.pixels.PixelTools;
import com.sksamuel.scrimage.pixels.PixelsExtractor;
import com.sksamuel.scrimage.transform.Transform;
import org.apache.commons.io.IOUtils;
import thirdparty.colorthief.ColorThief;
import thirdparty.colorthief.MMCQ;
import thirdparty.mortennobel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

/**
 * An immutable Image backed by an AWT BufferedImage.
 * <p>
 * An Image represents an abstraction that allow operations such
 * as resize, scale, rotate, flip, trim, pad, cover, fit.
 * <p>
 * All operations on an ImmutableImage are read only or return a cloned instance of this image.
 * For operations that can be performed without a copying step, see MutableImage.
 *
 * @author Stephen Samuel
 */
@SuppressWarnings("unused")
public class ImmutableImage extends MutableImage {

   private final ImageMetadata metadata;

   static {
      ImageIO.scanForPlugins();
   }

   public static final int DEFAULT_DATA_TYPE = BufferedImage.TYPE_INT_ARGB;
   // use DEFAULT_DATA_TYPE
   @Deprecated
   public static final int CANONICAL_DATA_TYPE = DEFAULT_DATA_TYPE;
   public static final int UNDERLYING_DATA_TYPE = -1;

   public static ImmutableImageLoader loader() {
      return new ImmutableImageLoader();
   }

   private ImmutableImage(BufferedImage awt, ImageMetadata metadata) {
      super(awt);
      this.metadata = metadata;
   }

   // ----- Image Builder Methods ------ //

   /**
    * Create a new [ImmutableImage] from a source AWT Image.
    * This method will copy the source image so that modifications to the original
    * do not write forward to this image.
    *
    * @param awt the source AWT Image
    * @return a new ImmutableImage
    */
   public static ImmutableImage fromAwt(BufferedImage awt) {
      return fromAwt(awt, UNDERLYING_DATA_TYPE);
   }

   /**
    * Create a new [ImmutableImage] from a source AWT Image.
    * This method will copy the source image so that modifications to the original
    * do not write forward to this image.
    * <p>
    * If the given type is greater than 0, then a copy will be made using that type for the image.
    * If the param type is -1, then the source image type will be used, unless the source image
    * has type 0, in which case BufferedImage.TYPE_INT_ARGB will be used.
    *
    * @param awt the source AWT Image
    * @return a new ImmutableImage
    */
   public static ImmutableImage fromAwt(BufferedImage awt, int type) {
      int imageType = type;
      if (type == UNDERLYING_DATA_TYPE) {
         imageType = awt.getType();
      }
      if (imageType == 0) {
         imageType = BufferedImage.TYPE_INT_ARGB;
      }
      BufferedImage target = new BufferedImage(awt.getWidth(null), awt.getHeight(null), imageType);
      Graphics g2 = target.getGraphics();
      g2.drawImage(awt, 0, 0, null);
      g2.dispose();
      return new ImmutableImage(target, ImageMetadata.empty);
   }

   /**
    * Creates a new [ImmutableImage] from an AWT image by wrapping that source image.
    * Note: Modifications to the source will write forward to this image. In other words,
    * this method should not be used if the source is going to be shared. This method is
    * intended for when an AWT image is created as an intermediate step and never exposed.
    *
    * @param awt      the source AWT Image
    * @param metadata the image metadata
    * @return a new ImmutableImage
    */
   public static ImmutableImage wrapAwt(BufferedImage awt, ImageMetadata metadata) {
      return new ImmutableImage(awt, metadata);
   }

   /**
    * Creates a new [ImmutableImage] from an AWT image by wrapping that source image.
    * Note: Modifications to the source will write forward to this image. In other words,
    * this method should not be used if the source is going to be shared. This method is
    * intended for when an AWT image is created as an intermediate step and never exposed.
    *
    * @param awt the source AWT Image
    * @return a new ImmutableImage
    */
   public static ImmutableImage wrapAwt(BufferedImage awt) {
      return wrapAwt(awt, ImageMetadata.empty);
   }

   /**
    * Creates a new [ImmutableImage] from an AWT image by wrapping that source image.
    * Since the source buffer is wrapped, any changes to the original will write through.
    * If you require a completely independent and truely immutable image, then consider [fromAwt].
    * <p>
    * If the given awt image does not have the same type as requested, then this will
    * force the image to be copied using [fromAwt].
    *
    * @param awt  the source AWT Image
    * @param type the AWT image type to use. If the image is not in this format already it will be coped.
    *             specify UNDERLYING_DATA_TYPE if you want to use the original
    * @return a new Scrimage Image
    */
   public static ImmutableImage wrapAwt(BufferedImage awt, int type) {
      if (type == UNDERLYING_DATA_TYPE || awt.getType() == type) return new ImmutableImage(awt, ImageMetadata.empty);
      else return fromAwt(awt, type);
   }

   public static ImmutableImage wrapPixels(int w, int h, Pixel[] pixels, ImageMetadata metadata) {
      return ImmutableImage.create(w, h, pixels).associateMetadata(metadata);
   }

   /**
    * Creates a new [ImmutableImage] from a file.
    * This method will also attach metadata.
    *
    * @deprecated use ImmutableImage.loader().fromFile(file);
    */
   @Deprecated
   public static ImmutableImage fromFile(File file) throws IOException {
      return fromPath(file.toPath());
   }

   /**
    * Creates a new [ImmutableImage] from a path.
    * This method will also attach metadata.
    *
    * @deprecated use ImmutableImage.loader().fromPath(path);
    */
   @Deprecated
   public static ImmutableImage fromPath(Path path) throws IOException {
      assert path != null;
      try (InputStream in = Files.newInputStream(path)) {
         return fromStream(in);
      }
   }

   /**
    * Create a new ImmutableImage that is the given width and height with no initialization.
    * This will usually result in a default black background (all pixel data defaulting to zeroes)
    * but that is not guaranteed.
    * The type of the image will be [ImmutableImage.DEFAULT_DATA_TYPE].
    *
    * @param width  the width of the new image
    * @param height the height of the new image
    * @return the new Image with the given width and height
    */
   public static ImmutableImage create(int width, int height) {
      return create(width, height, DEFAULT_DATA_TYPE);
   }

   /**
    * Create a new ImmutableImage that is the given width and height and type with no initialization.
    * This will usually result in a default black background (all pixel data defaulting to zeroes)
    * but that is not guaranteed.
    * The underlying image will have the type specified.
    *
    * @param width  the width of the new image
    * @param height the height of the new image
    * @return the new Image with the given width and height
    */
   public static ImmutableImage create(int width, int height, int type) {
      BufferedImage target = new BufferedImage(width, height, type);
      return new ImmutableImage(target, ImageMetadata.empty);
   }

   /**
    * Return a new ImmutableImage with the given width and height, with all pixels set to the supplied colour.
    * The type of the image will be [ImmutableImage.CANONICAL_DATA_TYPE].
    *
    * @param width  the width of the new Image
    * @param height the height of the new Image
    * @param color  the color to set all pixels to
    * @return the new Image
    */
   public static ImmutableImage filled(int width, int height, Color color) {
      return filled(width, height, color, ImmutableImage.DEFAULT_DATA_TYPE);
   }

   /**
    * Return a new ImmutableImage with the given width and height and type, with all pixels
    * set to the supplied colour.
    *
    * @param width  the width of the new Image
    * @param height the height of the new Image
    * @param color  the color to set all pixels to
    * @return the new Image
    */
   public static ImmutableImage filled(int width, int height, Color color, int type) {
      ImmutableImage target = create(width, height, type);
      for (int w = 0; w < width; w++) {
         for (int h = 0; h < height; h++) {
            target.awt().setRGB(w, h, RGBColor.fromAwt(color).toARGBInt());
         }
      }
      return target;
   }

   /**
    * Create a new Image from an input stream. This is intended to create
    * an image from an image format eg PNG, not from a stream of pixels.
    * This method will also attach metadata if available.
    *
    * @param in the stream to read the bytes from
    * @return a new Image
    * @deprecated use ImmutableImage.loader().fromFile(file);
    */
   @Deprecated
   public static ImmutableImage fromStream(InputStream in) throws IOException {
      return fromStream(in, UNDERLYING_DATA_TYPE);
   }

   /**
    * Create a new Image from an input stream. This is intended to create
    * an image from an image format eg PNG, not from a stream of pixels.
    * This method will also attach metadata if available.
    * The image will have the given type. If the given type is not the same
    * as stored in the stream, this will force the image to be copied.
    * Consider using fromStream(in).
    *
    * @param in the stream to read the bytes from
    * @return a new Image
    * @deprecated use ImmutableImage.loader().fromFile(file);
    */
   @Deprecated
   public static ImmutableImage fromStream(InputStream in, int type) throws IOException {
      assert in != null;
      byte[] bytes = IOUtils.toByteArray(in);
      assert bytes.length > 0;
      return parse(bytes, type);
   }

   /**
    * Creates a new Image from the resource on the classpath.
    *
    * @deprecated use ImmutableImage.loader().fromResource(file);
    */
   @Deprecated
   public static ImmutableImage fromResource(String path) throws IOException {
      return fromResource(path, DEFAULT_DATA_TYPE);
   }

   /**
    * @deprecated use ImmutableImage.loader().fromResource(file);
    */
   @Deprecated
   public static ImmutableImage fromResource(String path, int type) throws IOException {
      return fromStream(ImmutableImage.class.getResourceAsStream(path), type);
   }

   /**
    * Create a new Image from an array of pixels. The specified
    * width and height must match the number of pixels.
    * The image's type will be CANONICAL_DATA_TYPE.
    *
    * @return a new Image
    */
   public static ImmutableImage create(int w, int h, Pixel[] pixels) {
      return create(w, h, pixels, DEFAULT_DATA_TYPE);
   }

   /**
    * Create a new Image from an array of pixels with the given type. The specified
    * width and height must match the number of pixels.
    *
    * @return a new Image
    */
   public static ImmutableImage create(int w, int h, Pixel[] pixels, int type) {
      assert w * h == pixels.length;
      ImmutableImage image = ImmutableImage.create(w, h, type);
      image.mapInPlace((pixel) -> pixels[PixelTools.coordsToOffset(pixel.x, pixel.y, w)].toColor().awt());
      return image;
   }

   /**
    * Create a new Image from an array of bytes. This is intended to create
    * an image from an image format eg PNG, not from a stream of pixels.
    *
    * @param bytes the bytes from the format stream
    * @return a new Image
    * @deprecated use ImmutableImage.loader().fromBytes(file);
    */
   @Deprecated
   public static ImmutableImage parse(byte[] bytes) throws IOException {
      return parse(bytes, -1);
   }

   /**
    * Create a new Image from an array of bytes. This is intended to create
    * an image from an image format eg PNG, not from a stream of pixels.
    * <p>
    * The final image will have the given type. Note, if the image is not
    * stored in the given type, then it will need to be copied. Unless you have
    * a reason to specify the format, use parse(byte[]) and allow the image readers
    * to choose the most appropriate type.
    *
    * @param bytes the bytes from the format stream
    * @return a new Image
    * @deprecated use ImmutableImage.loader().fromBytes(file);
    */
   @Deprecated
   public static ImmutableImage parse(byte[] bytes, int type) throws IOException {
      ImmutableImage image = ImageReaders.read(new ByteArrayImageSource(bytes), null);
      ImageMetadata metadata = ImageMetadata.fromBytes(bytes);
      if (type > 0) {
         image = image.copy(type);
      }
      // detect iphone mode, and rotate
      return OrientationTools.reorient(image, metadata).associateMetadata(metadata);
   }

   // ----- End Builder Methods ------ //

   private PixelsExtractor pixelExtractor() {
      return r -> pixels(r.x, r.y, r.width, r.height);
   }

   public ImmutableImage autocrop() {
      return autocrop(Colors.Transparent.awt());
   }

   public ImmutableImage autocrop(Color color) {
      return autocrop(color, 0);
   }

   /**
    * Crops an image by removing cols and rows that are composed only of a single
    * given color.
    * <p>
    * Eg, if an image had a 20 pixel row of white at the top, and this method was
    * invoked with Color.WHITE then the image returned would have that 20 pixel row
    * removed.
    * <p>
    * This method is useful when images have an abudance of a single colour around them.
    *
    * @param color          the color to match
    * @param colorTolerance the amount of tolerance to use when determining whether the color matches the reference color [0..255]
    */
   public ImmutableImage autocrop(Color color, int colorTolerance) {
      int x1 = AutocropOps.scanright(color, height, width, 0, pixelExtractor(), colorTolerance);
      int x2 = AutocropOps.scanleft(color, height, width, width - 1, pixelExtractor(), colorTolerance);
      int y1 = AutocropOps.scandown(color, height, width, 0, pixelExtractor(), colorTolerance);
      int y2 = AutocropOps.scanup(color, height, width, height - 1, pixelExtractor(), colorTolerance);
      return subimage(x1, y1, x2 - x1, y2 - y1);
   }

   /**
    * Creates an empty ImmutableImage of the same concrete type as this image and with the same dimensions.
    * The underlying pixels will not be initialized.
    *
    * @return a new Image that is a clone of this image but with uninitialized data
    */
   public ImmutableImage blank() {
      return create(width, height, getType());
   }

   /**
    * Convenience method for bound(w, h, ScaleMethod.Bicubic)
    */
   public ImmutableImage bound(int w, int h) {
      return bound(w, h, ScaleMethod.Bicubic);
   }

   /**
    * Returns an image that is no larger than the given width and height keeping the same aspect ratio.
    * <p>
    * If the current image is already within the given dimensions then the same image will be returned.
    * If not, then a scaled image, with the same aspect ratio as the original, and with dimensions
    * inside the constraints will be returned.
    * <p>
    * This operation differs from max, in that max will scale an image up to be as large as it can be
    * inside the constraints. Bound will keep the image the same if its already within the constraints.
    *
    * @param w the maximum width
    * @param h the maximum height
    * @return the constrained image.
    */
   public ImmutableImage bound(int w, int h, ScaleMethod scaleMethod) {
      if (width <= w && height <= h) return this;
      else return max(w, h, scaleMethod);
   }

   /**
    * Returns a new Image with the brightness adjusted.
    */
   public ImmutableImage brightness(double factor) {
      ImmutableImage target = copy();
      target.rescaleInPlace(factor);
      return target;
   }

   /**
    * Creates a new image with the same data as this image.
    * Any operations to the copied image will not write back to the original.
    *
    * @return A copy of this image.
    */
   public ImmutableImage copy() {
      return fromAwt(awt());
   }

   /**
    * Returns a copy of this image with the underlying image type set to the given value.
    * The type value is one of the AWT standard image types, taken from BufferedImage.
    */
   public ImmutableImage copy(int type) {
      return fromAwt(awt(), type);
   }

   /**
    * Apply the given image with this image using the given composite.
    * The original image is unchanged.
    *
    * @param composite   the composite to use. See com.sksamuel.scrimage.Composite.
    * @param applicative the image to apply with the composite.
    * @return A new image with the given image applied using the given composite.
    */
   public ImmutableImage composite(Composite composite, ImmutableImage applicative) {
      ImmutableImage target = copy();
      composite.apply(target, applicative);
      return target;
   }

   public ImageMetadata getMetadata() {
      return metadata;
   }

   /**
    * Returns a new Image with the contrast adjusted.
    */
   public ImmutableImage contrast(double factor) {
      ImmutableImage target = copy();
      target.contrastInPlace(factor);
      return target;
   }

   /**
    * Convenience method for cover(targetWidth, targetHeight, ScaleMethod.Bicubic, Position.Center)
    */
   public ImmutableImage cover(int targetWidth,
                               int targetHeight) {
      return cover(targetWidth, targetHeight, ScaleMethod.Bicubic, Position.Center);
   }

   /**
    * Convenience method for cover(targetWidth, targetHeight, ScaleMethod.Bicubic, position)
    */
   public ImmutableImage cover(int targetWidth,
                               int targetHeight,
                               Position position) {
      return cover(targetWidth, targetHeight, ScaleMethod.Bicubic, position);
   }

   /**
    * Returns an image with the given dimensions where this image has been scaled to
    * completely cover the new dimensions whilst retaining the original aspect ratio.
    * <p>
    * If the given dimensions have a different aspect ratio than this image
    * then the image will be cropped so that it still covers the new area
    * without leaving any background.
    * <p>
    * In other words, the image "covers" the target dimensions with the potential loss of some of the image if
    * the aspect ratio change. Similar to taking a 16:9 movie and resizing it for a 4:3 screen. You can either
    * lose part of the image (this operation) or resize it so there is empty space on two sides (the fit operation).
    *
    * @param targetWidth  the target width
    * @param targetHeight the target height
    * @param scaleMethod  the type of scaling method to use. Defaults to Bicubic
    * @param position     where to position the image inside the new canvas
    * @return a new Image with the original image scaled to cover the new dimensions
    */
   public ImmutableImage cover(int targetWidth,
                               int targetHeight,
                               ScaleMethod scaleMethod,
                               Position position) {
      Dimension coveredDimensions = DimensionTools.dimensionsToCover(new Dimension(targetWidth, targetHeight), new Dimension(width, height));
      ImmutableImage scaled = scaleTo(coveredDimensions.getX(), coveredDimensions.getY(), scaleMethod);
      Dimension dim = position.calculateXY(targetWidth, targetHeight, coveredDimensions.getX(), coveredDimensions.getY());
      ImmutableImage result = create(targetWidth, targetHeight);
      result.overlayInPlace(scaled.awt(), dim.getX(), dim.getY());
      return result;
   }

   /**
    * Creates a new Image with the same dimensions of this image and with
    * all the pixels initialized to the given color.
    *
    * @return a new Image with the same dimensions as this
    */
   public ImmutableImage fill(Color color) {
      ImmutableImage target = blank();
      target.fillInPlace(color);
      return target;
   }

   /**
    * Creates a new Image with the same dimensions of this image and with
    * all the pixels initialized using the given painter.
    *
    * @return a new Image with the same dimensions as this
    */
   public ImmutableImage fill(Painter painter) {
      ImmutableImage target = blank();
      Painter temp = painter;
      if (painter instanceof LinearGradient) {
         LinearGradient linear = (LinearGradient) painter;
         if (linear.getX1() == Integer.MIN_VALUE && linear.getX2() == Integer.MAX_VALUE) {
            temp = new LinearGradient(0, 0, linear.getColor1(), width, 0, linear.getColor2());
         } else if (linear.getY1() == Integer.MIN_VALUE && linear.getY2() == Integer.MAX_VALUE) {
            temp = new LinearGradient(0, 0, linear.getColor1(), 0, height, linear.getColor2());
         }
      }
      Painter finalTemp = temp;
      target.toCanvas().drawInPlace(new FilledRect(0, 0, width, height, g2 -> g2.setPainter(finalTemp)));
      return target;
   }

   /**
    * Apply a sequence of filters in sequence.
    * This is sugar for image.filter(filter1).filter(filter2)....
    *
    * @param filters the sequence filters to apply
    * @return the result of applying each filter in turn
    */
   public ImmutableImage filter(Filter... filters) throws IOException {
      ImmutableImage image = this;
      for (Filter filter : filters) {
         image = image.filter(filter);
      }
      return image;
   }

   /**
    * Creates a copy of this image with the given filter applied.
    * The original (this) image is unchanged.
    *
    * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
    * @return A new image with the given filter applied.
    */
   public ImmutableImage filter(Filter filter) throws IOException {
      ImmutableImage target = copy();
      filter.apply(target);
      return target;
   }

   /**
    * Convenience method for:
    * fit(canvasWidth, canvasHeight, Colors.Transparent.toAWT(), ScaleMethod.Bicubic, Position.Center)
    */
   public ImmutableImage fit(int canvasWidth,
                             int canvasHeight) {
      return fit(canvasWidth, canvasHeight, Colors.Transparent.toAWT(), ScaleMethod.Bicubic, Position.Center);
   }

   public ImmutableImage fit(int canvasWidth,
                             int canvasHeight,
                             ScaleMethod scaleMethod) {
      return fit(canvasWidth, canvasHeight, Colors.Transparent.awt(), scaleMethod, Position.Center);
   }

   public ImmutableImage fit(int canvasWidth,
                             int canvasHeight,
                             Color color) {
      return fit(canvasWidth, canvasHeight, color, ScaleMethod.Bicubic, Position.Center);
   }

   public ImmutableImage fit(int canvasWidth,
                             int canvasHeight,
                             Color color,
                             Position position) {
      return fit(canvasWidth, canvasHeight, color, ScaleMethod.Bicubic, position);
   }

   /**
    * Returns a copy of this image with the given dimensions
    * where the original image has been scaled to fit completely
    * inside the new dimensions whilst retaining the original aspect ratio.
    * <p>
    * In other words, the image "fits" the target dimensions without losing any of the image if
    * the aspect ratio change. Similar to taking a 16:9 movie and resizing it for a 4:3 screen. You can either
    * lose part of the image (cover operation) or resize it so there is empty space on two sides (this operation).
    *
    * @param canvasWidth  the target width
    * @param canvasHeight the target height
    * @param scaleMethod  the algorithm to use for the scaling operation. See ScaleMethod.
    * @param color        the color to use as the "padding" colour should the scaled original not fit exactly inside the new dimensions
    * @param position     where to position the image inside the new canvas
    * @return a new Image with the original image scaled to fit inside
    */
   public ImmutableImage fit(int canvasWidth,
                             int canvasHeight,
                             Color color,
                             ScaleMethod scaleMethod,
                             Position position) {
      Dimension wh = DimensionTools.dimensionsToFit(new Dimension(canvasWidth, canvasHeight), new Dimension(width, height));
      Dimension dim = position.calculateXY(canvasWidth, canvasHeight, wh.getX(), wh.getY());
      ImmutableImage scaled = scaleTo(wh.getX(), wh.getY(), scaleMethod);
      ImmutableImage result = ImmutableImage.filled(canvasWidth, canvasHeight, color);
      result.overlayInPlace(scaled.awt(), dim.getX(), dim.getY());
      return result;
   }

   /**
    * Returns the most n used colours in this image.
    *
    * @param colours how many colours to quantize for.
    */
   public RGBColor[] quantize(int colours) {
      MMCQ.CMap cmap = ColorThief.getColorMap(this.awt(), colours);
      return Arrays.stream(cmap.palette()).map(RGBColor::fromRGB).toArray(RGBColor[]::new);
   }

   /**
    * Applies an affine transform in place.
    */
   private BufferedImage affineTransform(AffineTransform tx) {
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      return op.filter(awt(), null);
   }

   /**
    * Flips this image horizontally.
    *
    * @return The result of flipping this image horizontally.
    */
   public ImmutableImage flipX() {
      AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
      tx.translate(-width, 0);
      return wrapAwt(affineTransform(tx), metadata);
   }

   /**
    * Flips this image vertically.
    *
    * @return The result of flipping this image vertically.
    */
   public ImmutableImage flipY() {
      AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
      tx.translate(0, -height);
      return wrapAwt(affineTransform(tx), metadata);
   }

   /**
    * Convenience method for max(maxW, maxH, ScaleMethod.Bicubic)
    */
   public ImmutableImage max(int maxW, int maxH) {
      return max(maxW, maxH, ScaleMethod.Bicubic);
   }

   /**
    * Returns a new image that is scaled to fit the specified bounds while retaining the same aspect ratio
    * as the original image. The dimensions of the returned image will be the same as the result of the
    * scaling operation. That is, no extra padding will be added to match the bounded width and height.
    * <p>
    * For an operation that will scale an image as well as add padding to fit the dimensions perfectly, then use fit.
    * For an operation that will only resize smaller, and not larger, see bound.
    * <p>
    * Requesting a bound of 200,200 on an image of 300,600 will result in a scale to 100,200.
    * Eg, the original image will be scaled down to fit the bounds.
    * <p>
    * Requesting a bound of 150,200 on an image of 150,150 will result in the same image being returned.
    * Eg, the original image cannot be scaled up any further without exceeding the bounds.
    * <p>
    * Requesting a bound of 300,300 on an image of 100,150 will result in a scale to 200,300.
    * <p>
    * Requesting a bound of 100,1000 on an image of 50,50 will result in a scale to 100,100.
    *
    * @param maxW the maximum width
    * @param maxH the maximum height
    * @return A new image that is the result of the binding.
    */
   public ImmutableImage max(int maxW, int maxH, ScaleMethod scaleMethod) {
      Dimension dimensions = DimensionTools.dimensionsToFit(new Dimension(maxW, maxH), new Dimension(width, height));
      return scaleTo(dimensions.getX(), dimensions.getY(), scaleMethod);
   }

   public ImmutableImage op(BufferedImageOp op) {
      BufferedImage after = op.filter(awt(), null);
      return wrapAwt(after, metadata);
   }

   /**
    * Returns a new image that is the result of overlaying the given image over this image.
    * That is, the existing image ends up being "under" the image parameter.
    * The x / y parameters determine where the (0,0) coordinate of the overlay should be placed.
    * <p>
    * If the image to render exceeds the boundaries of the source image, then the excess
    * pixels will be ignored.
    *
    * @return a new Image with the given image overlaid.
    */
   public ImmutableImage overlay(AwtImage overlayImage) {
      return overlay(overlayImage, 0, 0);
   }

   public ImmutableImage overlay(AwtImage overlayImage, int x, int y) {
      ImmutableImage target = copy();
      target.overlayInPlace(overlayImage.awt(), x, y);
      return target;
   }

   public ImmutableImage resize(double scaleFactor) {
      return resize(scaleFactor, Position.Center, Colors.Transparent.awt());
   }

   public ImmutableImage resize(double scaleFactor, Position position) {
      return resize(scaleFactor, position, Colors.Transparent.awt());
   }

   public ImmutableImage resize(double scaleFactor, Color background) {
      return resize(scaleFactor, Position.Center, background);
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
    * @param position    where to position the original image after the canvas size change. Defaults to centre.
    * @param background  the color to use for expande background areas.
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resize(double scaleFactor, Position position, Color background) {
      return resizeTo((int) (width * scaleFactor), (int) (height * scaleFactor), position, background);
   }

   public ImmutableImage resizeTo(int targetWidth,
                                  int targetHeight) {
      return resizeTo(targetWidth, targetHeight, Position.Center, Colors.Transparent.awt());
   }

   public ImmutableImage resizeTo(int targetWidth,
                                  int targetHeight,
                                  Position position) {
      return resizeTo(targetWidth, targetHeight, position, Colors.Transparent.awt());
   }

   public ImmutableImage resizeTo(int targetWidth,
                                  int targetHeight,
                                  Color color) {
      return resizeTo(targetWidth, targetHeight, Position.Center, color);
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    * <p>
    * If the dimensions are smaller than the current canvas size
    * then the image will be cropped.
    * <p>
    * The position parameter determines how the original image will be positioned on the new
    * canvas.
    *
    * @param targetWidth  the target width
    * @param targetHeight the target height
    * @param position     where to position the original image after the canvas size change
    * @param background   the background color if the canvas was enlarged
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeTo(int targetWidth,
                                  int targetHeight,
                                  Position position,
                                  Color background) {
      if (targetWidth == width && targetHeight == height) return this;
      else {
         Dimension dim = position.calculateXY(targetWidth, targetHeight, width, height);
         return filled(targetWidth, targetHeight, background).overlay(this, dim.getX(), dim.getY());
      }
   }

   public ImmutableImage resizeToRatio(double targetRatio) {
      return resizeToRatio(targetRatio, Position.Center, Colors.Transparent.awt());
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    * <p>
    * Depending on ratio will increase either width or height.
    * <p>
    * The position parameter determines how the original image will be positioned on the new
    * canvas.
    *
    * @param targetRatio width divided by height
    * @param position    where to position the original image after the canvas size change
    * @param background  the background color if the canvas was enlarged
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeToRatio(double targetRatio, Position position, Color background) {
      double currRatio = ratio();
      if (currRatio == targetRatio) return this;
      else if (currRatio > targetRatio) {
         int targetHeight = (int) (width / targetRatio); // cannot be zero because it's larger than currRatio
         return resizeTo(width, targetHeight, position, background);
      } else {
         int targetWidth = (int) (height * targetRatio);
         return resizeTo(targetWidth, height, position, background);
      }
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeToHeight(int targetHeight) {
      return resizeToHeight(targetHeight, Position.Center, Colors.Transparent.toAWT());
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param position where to position the original image after the canvas size change
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeToHeight(int targetHeight, Position position, Color background) {
      return resizeTo((int) (targetHeight / (double) height * width), targetHeight, position, background);
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeToWidth(int targetWidth) {
      return resizeToWidth(targetWidth, Position.Center, Colors.Transparent.toAWT());
   }

   /**
    * Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param position where to position the original image after the canvas size change
    * @return a new Image that is the result of resizing the canvas.
    */
   public ImmutableImage resizeToWidth(int targetWidth, Position position, Color background) {
      return resizeTo(targetWidth, (int) (targetWidth / (double) width * height), position, background);
   }

   /**
    * Returns a copy of this image rotated 90 degrees anti-clockwise (counter clockwise to US English speakers).
    */
   public ImmutableImage rotateLeft() {
      Radians angle = new Radians(-Math.PI / 2);
      return rotate(angle);
   }

   /**
    * Returns a copy of this image rotated 90 degrees clockwise.
    */
   public ImmutableImage rotateRight() {
      Radians angle = new Radians(Math.PI / 2);
      return rotate(angle);
   }

   public ImmutableImage rotate(Radians radians) {
      return wrapAwt(super.rotateByRadians(radians), metadata);
   }

   public ImmutableImage rotate(Degrees degrees) {
      return rotate(degrees.toRadians());
   }

   /**
    * Creates a new image which is the result of this image
    * padded with the given number of pixels on each edge.
    * <p>
    * Eg, requesting a pad of 30 on an image of 250,300 will result
    * in a new image with a canvas size of 310,360
    *
    * @param size  the number of pixels to add on each edge
    * @param color the background of the padded area.
    * @return A new image that is the result of the padding
    */
   public ImmutableImage pad(int size, Color color) {
      return padTo(width + size * 2, height + size * 2, color);
   }

   public ImmutableImage padTo(int targetWidth, int targetHeight) {
      return padTo(targetWidth, targetHeight, Colors.Transparent.awt());
   }

   /**
    * Creates a new ImmutableImage which is the result of this image padded to the canvas size specified.
    * If this image is already larger than the specified dimensions then the sizes of the existing
    * image will be used instead.
    * <p>
    * Eg, requesting a pad of 200,200 on an image of 250,300 will result
    * in keeping the 250,300.
    * <p>
    * Eg2, requesting a pad of 300,300 on an image of 400,250 will result
    * in the width staying at 400 and the height padded to 300.
    *
    * @param targetWidth  the size of the output canvas width
    * @param targetHeight the size of the output canvas height
    * @param color        the background of the padded area.
    * @return A new image that is the result of the padding
    */
   public ImmutableImage padTo(int targetWidth, int targetHeight, Color color) {
      int w = Math.max(width, targetWidth);
      int h = Math.max(height, targetHeight);
      int x = (int) ((w - width) / 2.0);
      int y = (int) ((h - height) / 2.0);
      return padWith(x, y, w - width - x, h - height - y, color);
   }

   public ImmutableImage padWith(int left, int top, int right, int bottom) {
      return padWith(left, top, right, bottom, Colors.Transparent.awt());
   }

   /**
    * Creates a new ImmutableImage by adding the given number of columns/rows on left, top, right and bottom.
    *
    * @param left   the number of columns/pixels to add on the left
    * @param top    the number of rows/pixels to add to the top
    * @param right  the number of columns/pixels to add on the right
    * @param bottom the number of rows/pixels to add to the bottom
    * @param color  the background of the padded area.
    * @return A new image that is the result of the padding operation.
    */
   public ImmutableImage padWith(int left, int top, int right, int bottom, Color color) {
      int w = width + left + right;
      int h = height + top + bottom;
      return filled(w, h, color).overlay(this, left, top);
   }

   /**
    * Creates a new Image by adding k amount of rows of pixels to the top.
    * This is a convenience method for calling padWith(0,k,0,0,color).
    *
    * @param k     the number of rows to pad by
    * @param color the color that should be used for the new rows
    * @return the new Image
    */
   public ImmutableImage padTop(int k, Color color) {
      return padWith(0, k, 0, 0, color);
   }

   /**
    * Creates a new Image by adding k amount of rows of pixels to the bottom.
    * This is a convenience method for calling padWith(0,0,0,k,color).
    *
    * @param k     the number of rows to pad by
    * @param color the color that should be used for the new rows
    * @return the new Image
    */
   public ImmutableImage padBottom(int k, Color color) {
      return padWith(0, 0, 0, k, color);
   }

   /**
    * Creates a new Image by adding k columns of pixels to the left.
    * This is a convenience method for calling padWith(k,0,0,0,Transparent).
    *
    * @param k the number of columns to pad by
    * @return the new Image
    */
   public ImmutableImage padLeft(int k) {
      return padLeft(k, Colors.Transparent.toAWT());
   }

   /**
    * Creates a new Image by adding k columns of pixels to the left.
    * This is a convenience method for calling padWith(k,0,0,0,color).
    *
    * @param k     the number of columns to pad by
    * @param color the color that should be used for the new pixels
    * @return the new Image
    */
   public ImmutableImage padLeft(int k, Color color) {
      return padWith(k, 0, 0, 0, color);
   }

   /**
    * Creates a new Image by adding k columns of pixels to the right.
    * This is a convenience method for calling padWith(0,0,k,0,color).
    *
    * @param k the number of columns to pad by
    * @return the new Image
    */
   public ImmutableImage padRight(int k) {
      return padRight(k, Colors.Transparent.toAWT());
   }

   /**
    * Creates a new Image by adding k columns of pixels to the right.
    * This is a convenience method for calling padWith(0,0,k,0,color).
    *
    * @param k     the number of columns to pad by
    * @param color the color that should be used for the new pixels
    * @return the new Image
    */
   public ImmutableImage padRight(int k, Color color) {
      return padWith(0, 0, k, 0, color);
   }

   /**
    * Returns a new ImmutableImage with transparency replaced with the given color.
    */
   public ImmutableImage removeTransparency(Color color) {
      ImmutableImage target = copy();
      target.replaceTransparencyInPlace(color);
      return target;
   }

   /**
    * Convenience method for scaleToWidth(targetWith, scaleMethod)
    */
   public ImmutableImage scaleToWidth(int targetWidth) {
      return scaleToWidth(targetWidth, ScaleMethod.Bicubic);
   }

   /**
    * Scale will resize the canvas and scale the image to match.
    * This is like a "image resize" in Photoshop.
    * <p>
    * scaleToWidth will scale the image so that the new image has a width that matches the
    * given targetWidth and the height is determined by the original aspect ratio.
    * <p>
    * Eg, an image of 200,300 with a scaleToWidth of 400 will result
    * in a scaled image of 400,600
    *
    * @param targetWidth the target width
    * @param scaleMethod the type of scaling method to use.
    * @return a new Image that is the result of scaling this image
    */
   public ImmutableImage scaleToWidth(int targetWidth, ScaleMethod scaleMethod) {
      return scaleToWidth(targetWidth, scaleMethod, true);
   }

   public ImmutableImage scaleToWidth(int targetWidth, ScaleMethod scaleMethod, boolean keepAspectRatio) {
      int targetHeight = keepAspectRatio ? (int) (targetWidth / (double) width * height) : height;
      return scaleTo(targetWidth, targetHeight, scaleMethod);
   }

   /**
    * Convenience method for scaleToHeight(targetHeight, ScaleMethod.Bicubic)
    */
   public ImmutableImage scaleToHeight(int targetHeight) {
      return scaleToHeight(targetHeight, ScaleMethod.Bicubic);
   }

   /**
    * Convenience method for scaleToHeight(targetHeight, scaleMethod, true)
    */
   public ImmutableImage scaleToHeight(int targetHeight, ScaleMethod scaleMethod) {
      return scaleToHeight(targetHeight, scaleMethod, true);
   }

   /**
    * Scale will resize the canvas and scale the image to match.
    * This is like a "image resize" in Photoshop.
    * <p>
    * scaleToHeight will scale the image so that the new image has a height that matches the
    * given targetHeight.
    * <p>
    * If keepAspectRatio is true, then the width will also be scaled so that the aspect ratio
    * of the image does not change.
    * If keepAspectRatio is false, then the width will stay the same.
    * <p>
    * Eg, an image of 200,300 with a scaleToHeight of 450 and keepAspectRatio of true will result
    * in a scaled image of 300,450 (because 300 to 450 is 1.5 and so 200 x 1.5 is 300).
    *
    * @param targetHeight    the target height
    * @param scaleMethod     the type of scaling method to use.
    * @param keepAspectRatio whether the width should also be scaled to keep the aspect ratio the same.
    * @return a new Image that is the result of scaling this image
    */
   public ImmutableImage scaleToHeight(int targetHeight, ScaleMethod scaleMethod, boolean keepAspectRatio) {
      int targetWidth = keepAspectRatio ? (int) (targetHeight / (double) height * width) : width;
      return scaleTo(targetWidth, targetHeight, scaleMethod);
   }

   public ImmutableImage scaleHeightToRatio(double ratio) {
      return scaleHeightToRatio(ratio, ScaleMethod.Bicubic);
   }

   public ImmutableImage scaleHeightToRatio(double ratio, ScaleMethod scaleMethod) {
      return scaleToHeight((int) (width * ratio), scaleMethod);
   }

   public ImmutableImage scale(double scaleFactor) {
      return scale(scaleFactor, ScaleMethod.Bicubic);
   }

   /**
    * Scale will resize the canvas and the image.
    * This is like a "image resize" in Photoshop.
    *
    * @param scaleFactor the target increase or decrease. 1 is the same as original.
    * @param scaleMethod the type of scaling method to use.
    * @return a new Image that is the result of scaling this image
    */
   public ImmutableImage scale(double scaleFactor, ScaleMethod scaleMethod) {
      return scaleTo((int) (width * scaleFactor), (int) (height * scaleFactor), scaleMethod);
   }

   /**
    * Convenience method for scaleTo(targetWidth, targetHeight, ScaleMethod.Bicubic);
    */
   public ImmutableImage scaleTo(int targetWidth,
                                 int targetHeight) {
      return scaleTo(targetWidth, targetHeight, ScaleMethod.Bicubic);
   }

   /**
    * Scale will resize both the canvas and the image.
    * This is like a "image resize" in Photoshop.
    * <p>
    * The size of the scaled instance are taken from the given
    * width and height parameters.
    *
    * @param targetWidth  the target width
    * @param targetHeight the target height
    * @param scaleMethod  the type of scaling method to use. Defaults to SmoothScale
    * @return a new Image that is the result of scaling this image
    */
   public ImmutableImage scaleTo(int targetWidth,
                                 int targetHeight,
                                 ScaleMethod scaleMethod) {
      if (targetWidth == width && targetHeight == height) return this;
      switch (scaleMethod) {
         case FastScale:
            if (targetWidth < width && targetHeight < height && awt().getType() == BufferedImage.TYPE_INT_ARGB)
               return wrapAwt(fastScaleScrimage(targetWidth, targetHeight), metadata);
            else
               return wrapAwt(fastScaleAwt(targetWidth, targetHeight), metadata);
         case Lanczos3:
            Lanczos3Filter lan = ResampleFilters.lanczos3Filter;
            ImmutableImage s1 = op(new ResampleOp(lan, targetWidth, targetHeight));
            return wrapAwt(s1.awt(), s1.awt().getType());
         case BSpline:
            BSplineFilter bs = ResampleFilters.bSplineFilter;
            ImmutableImage s2 = op(new ResampleOp(bs, targetWidth, targetHeight));
            return wrapAwt(s2.awt(), s2.awt().getType());
         case Bilinear:
            TriangleFilter t = ResampleFilters.triangleFilter;
            ImmutableImage s3 = op(new ResampleOp(t, targetWidth, targetHeight));
            return wrapAwt(s3.awt(), s3.awt().getType());
         case Progressive:
            if (targetWidth >= width && targetHeight >= height) return scaleTo(targetWidth, targetHeight, ScaleMethod.Bicubic);
            BufferedImage result = ProgressiveScale.scale(awt(), targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            return wrapAwt(result);
         case Bicubic:
            BiCubicFilter b = ResampleFilters.biCubicFilter;
            ImmutableImage s4 = op(new ResampleOp(b, targetWidth, targetHeight));
            return wrapAwt(s4.awt());
         default:
            throw new UnsupportedOperationException();
      }
   }

   /**
    * Extracts a subimage, but using subpixel interpolation.
    */
   public ImmutableImage subpixelSubimage(double x,
                                          double y,
                                          int subWidth,
                                          int subHeight) {
      assert x >= 0;
      assert x + subWidth < width;
      assert y >= 0;
      assert y + subHeight < height;

      Pixel[] matrix = new Pixel[subWidth * subHeight];
      // Simply copy the pixels over, one by one.
      for (int yIndex = 0; yIndex < subHeight; yIndex++) {
         for (int xIndex = 0; xIndex < subWidth; xIndex++) {
            matrix[PixelTools.coordsToOffset(xIndex, yIndex, subWidth)] = new Pixel(xIndex, yIndex, subpixel(xIndex + x, yIndex + y));
         }
      }
      return wrapPixels(subWidth, subHeight, matrix, metadata);
   }

   /**
    * Extract a patch, centered at a subpixel point.
    */
   public ImmutableImage subpixelSubimageCenteredAtPoint(double x,
                                                         double y,
                                                         double xRadius,
                                                         double yRadius) {
      double xWidth = 2 * xRadius;
      double yWidth = 2 * yRadius;

      // The dimensions of the extracted patch must be integral.
      assert xWidth == Math.round(xWidth);
      assert yWidth == Math.round(yWidth);

      return subpixelSubimage(
         x - xRadius,
         y - yRadius,
         (int) Math.round(xWidth),
         (int) Math.round(yWidth));
   }

   /**
    * Returns a new Image that is a subimage or region of the original image.
    *
    * @param x the start x coordinate
    * @param y the start y coordinate
    * @param w the width of the subimage
    * @param h the height of the subimage
    * @return a new Image that is the subimage
    */
   public ImmutableImage subimage(int x, int y, int w, int h) {
      if (w <= 0) throw new RuntimeException("Width cannot be <= 0");
      if (h <= 0) throw new RuntimeException("Height cannot be <= 0");
      return wrapPixels(w, h, pixels(x, y, w, h), metadata);
   }

   public ImmutableImage subimage(Rectangle rectangle) {
      if (rectangle == null)
         return this;
      else
         return subimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
   }

   /**
    * Returns a new Image which is the source image, but only keeping a max of k columns from the left.
    */
   public ImmutableImage takeLeft(int k) {
      return subimage(0, 0, Math.min(k, width), height);
   }

   /**
    * Returns a new Image which is the source image, but only keeping a max of k columns from the right.
    */
   public ImmutableImage takeRight(int k) {
      // trim k to the max we can use, which is the width
      int maxK = Math.min(k, width);
      return subimage(width - maxK, 0, maxK, height);
   }

   /**
    * Returns a new Image which is the source image, but only keeping a max of k rows from the top.
    */
   public ImmutableImage takeTop(int k) {
      return subimage(0, 0, width, Math.min(k, height));
   }

   /**
    * Returns a new Image which is the source image, but only keeping a max of k rows from the bottom.
    */
   public ImmutableImage takeBottom(int k) {
      // trim k to the max we can use, which is the height
      int maxK = Math.min(k, height);
      return subimage(0, height - maxK, width, maxK);
   }

   /**
    * Returns an image that is the result of translating the image while keeping the same
    * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
    * This would mean 10 columns and 5 rows of background added to the left and top.
    * <p>
    * This method will use transparency for the color of the displaced pixels.
    *
    * @return a new Image with this image translated.
    */
   public ImmutableImage translate(int x, int y) {
      return translate(x, y, Colors.Transparent.toAWT());
   }

   /**
    * Returns an image that is the result of translating the image while keeping the same
    * view window. Eg, if translating by 10,5 then all pixels will move 10 to the right, and 5 down.
    * This would mean 10 columns and 5 rows of background added to the left and top.
    *
    * @return a new Image with this image translated.
    */
   public ImmutableImage translate(int x, int y, Color bg) {
      return fill(bg).overlay(this, x, y);
   }

   /**
    * Removes the given amount of pixels from each edge; like a crop operation.
    *
    * @param amount the number of pixels to trim from each edge
    * @return a new Image with the dimensions width-trim*2, height-trim*2
    */
   public ImmutableImage trim(int amount) {
      return trim(amount, amount, amount, amount);
   }

   /**
    * Removes the given amount of pixels from each edge; like a crop operation.
    *
    * @param left   the number of pixels to trim from the left
    * @param top    the number of pixels to trim from the top
    * @param right  the number of pixels to trim from the right
    * @param bottom the number of pixels to trim from the bottom
    * @return a new Image with the dimensions width-trim*2, height-trim*2
    */
   public ImmutableImage trim(int left, int top, int right, int bottom) {
      return create(width - left - right, height - bottom - top).overlay(this, -left, -top);
   }

   /**
    * Removes the specified amount of columns of pixels from the left side of the image.
    * This is a convenience method for trim(k,0,0,0).
    *
    * @param k how many columns of pixels to remove
    * @return a new image with k columns from the left removed
    */
   public ImmutableImage trimLeft(int k) {
      return trim(k, 0, 0, 0);
   }

   /**
    * Removes the specified amount of columns of pixels from the right side of the image.
    * This is a convenience method for trim(0,0,k,0).
    *
    * @param k how many columns of pixels to remove
    * @return a new image with k columns from the right removed
    */
   public ImmutableImage trimRight(int k) {
      return trim(0, 0, k, 0);
   }

   /**
    * Removes the specified amount of rows of pixels from the bottom of the image.
    * This is a convenience method for trim(0,0,0,k).
    *
    * @param k how many rows of pixels to remove
    * @return a new image with k rows from the bottom removed
    */
   public ImmutableImage trimBottom(int k) {
      return trim(0, 0, 0, k);
   }

   /**
    * Removes the specified amount of rows of pixels from the top of the image.
    * This is a convenience method for trim(0,k,0,0).
    *
    * @param k how many rows of pixels to remove
    * @return a new image with k rows from the top removed
    */
   public ImmutableImage trimTop(int k) {
      return trim(0, k, 0, 0);
   }

   public ImmutableImage underlay(ImmutableImage underlayImage) {
      return underlay(underlayImage, 0, 0);
   }

   /**
    * Returns a new Image that is the result of overlaying this image over the supplied image.
    * That is, the existing image ends up being "on top" of the image parameter.
    * The x / y parameters determine where the (0,0) coordinate of the overlay should be placed.
    * <p>
    * If the image to render exceeds the boundaries of the source image, then the excess
    * pixels will be ignored.
    *
    * @return a new Image with the given image overlaid.
    */
   public ImmutableImage underlay(ImmutableImage underlayImage, int x, int y) {
      ImmutableImage target = this.blank();
      target.overlayInPlace(underlayImage.awt(), x, y);
      target.overlayInPlace(awt(), x, y);
      return target;
   }

   /**
    * Returns this image, with metadata attached.
    * <p>
    * Both the original and the new image will share a buffer
    */
   public ImmutableImage associateMetadata(ImageMetadata metadata) {
      return new ImmutableImage(awt(), metadata);
   }

   public ImmutableImage zoom(double factor) {
      return zoom(factor, ScaleMethod.Bicubic);
   }

   /**
    * Returns a new image that is the result of scaling this image but without changing the canvas size.
    * <p>
    * This can be thought of as zooming in on a camera - the viewpane does not change but the image increases
    * in size with the outer columns/rows being dropped as required.
    *
    * @param factor how much to zoom by
    * @param method how to apply the scaling method
    * @return the zoomed image
    */
   public ImmutableImage zoom(double factor, ScaleMethod method) {
      return scale(factor, method).resizeTo(width, height, Position.Center, Color.WHITE);
   }

   /**
    * Maps the pixels of this image into another image by applying the given function to each pixel.
    * <p>
    * The function accepts a pixel being transformed and returns a new (or same) color.
    *
    * @param mapper the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
    */
   public ImmutableImage map(Function<Pixel, Color> mapper) {
      ImmutableImage target = copy();
      target.mapInPlace(mapper);
      return target;
   }

   /**
    * Returns a Canvas that wraps this image.
    */
   public Canvas toCanvas() {
      return new Canvas(this);
   }

   @Override
   public WriteContext forWriter(ImageWriter writer) {
      return new WriteContext(writer, this, metadata);
   }

   public ImmutableImage overlay(ImmutableImage image, Position position) {
      ImmutableImage copy = this.copy();
      copy.overlayInPlace(
         image.awt(),
         position.calculateX(copy.width, copy.height, image.width, image.height),
         position.calculateY(copy.width, copy.height, image.width, image.height)
      );
      return copy;
   }

   public ImmutableImage transform(Transform transform) throws IOException {
      return transform.apply(this);
   }
}
