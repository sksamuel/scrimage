package com.sksamuel.scrimage;

import com.sksamuel.scrimage.angles.Radians;
import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.WriteContext;
import com.sksamuel.scrimage.pixels.Pixel;
import com.sksamuel.scrimage.pixels.PixelTools;
import com.sksamuel.scrimage.scaling.AwtNearestNeighbourScale;
import com.sksamuel.scrimage.scaling.Scale;
import com.sksamuel.scrimage.scaling.ScrimageNearestNeighbourScale;
import com.sksamuel.scrimage.subpixel.LinearSubpixelInterpolator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Wraps an AWT BufferedImage with some basic helper functions related to sizes, pixels etc.
 * It also includes methods to write out the image.
 * None of the operations in this class will mutate the underlying awt buffer.
 */
public class AwtImage {

   private final BufferedImage awt;
   public final int width;
   public final int height;

   public AwtImage(BufferedImage awt) {
      assert awt != null;
      this.awt = awt;
      width = awt.getWidth();
      height = awt.getHeight();
   }

   public ImmutableImage toImmutableImage() {
      return ImmutableImage.wrapAwt(awt);
   }

   /**
    * Returns the [BufferedImage] that this Image is wrapping.
    */
   public BufferedImage awt() {
      return awt;
   }

   /**
    * The centre coordinates for the image.
    */
   public Point center() {
      return new Point(width / 2, height / 2);
   }

   public int centreX() {
      return center().x;
   }

   public int centreY() {
      return center().y;
   }

   /**
    * The radius of the image defined as the centre to the corners.
    */
   public int radius() {
      return (int) Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height / 2.0, 2));
   }

   public Dimension dimensions() {
      return new Dimension(width, height);
   }

   /**
    * @return Returns the aspect ratio for this image.
    */
   public double ratio() {
      if (height == 0) return 0;
      else return width / (double) height;
   }

   /**
    * Returns the AWT type of this image.
    */
   public int getType() {
      return awt().getType();
   }

   /**
    * Returns the colors of this image represented as an array of RGBColor.
    */
   public RGBColor[] colors() {
      return Arrays.stream(pixels()).map(Pixel::toColor).toArray(RGBColor[]::new);
   }

   /**
    * Returns the pixels of this image represented as an array of Pixels.
    */
   public Pixel[] pixels() {
      DataBuffer buffer = awt().getRaster().getDataBuffer();
      if (buffer instanceof DataBufferInt) {
         DataBufferInt intbuffer = (DataBufferInt) buffer;
         int[] data = intbuffer.getData();
         int index = 0;
         Pixel[] pixels = new Pixel[data.length];
         if (awt().getType() == BufferedImage.TYPE_INT_ARGB) {
            while (index < data.length) {
               Point point = PixelTools.offsetToPoint(index, width);
               pixels[index] = new Pixel(point.x, point.y, data[index]);
               index++;
            }
         } else if (awt().getType() == BufferedImage.TYPE_INT_RGB) {
            while (index < data.length) {
               Point point = PixelTools.offsetToPoint(index, width);
               pixels[index] = new Pixel(point.x, point.y, data[index]);
               index++;
            }
         } else if (awt().getType() == BufferedImage.TYPE_4BYTE_ABGR) {
            while (index < data.length) {
               Point point = PixelTools.offsetToPoint(index, width);
               pixels[index / 4] = new Pixel(point.x, point.y, data[index / 4]);
               index = index + 4;
            }
         } else {
            throw new RuntimeException("Unsupported image type " + awt().getType());
         }
         return pixels;
      } else {
         Pixel[] pixels = new Pixel[count()];
         int index = 0;
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               pixels[index++] = new Pixel(x, y, awt().getRGB(x, y));
            }
         }
         return pixels;
      }
   }

   /**
    * Returns the pixels of the image as an iterator.
    * <p>
    * The iterator is the most efficient way to lazily iterator over the pixels as the pixels will only
    * be fetched from the raster as needed.
    *
    * @return the iterator
    */
   public Iterator<Pixel> iterator() {
      return new Iterator<Pixel>() {

         private int k = 0;

         @Override
         public boolean hasNext() {
            return k < AwtImage.this.count();
         }

         @Override
         public Pixel next() {
            Point point = PixelTools.offsetToPoint(k++, width);
            int rgb = awt.getRGB(point.x, point.y);
            return new Pixel(point.x, point.y, rgb);
         }
      };
   }

   // This tuple contains all the state that identifies this particular image.
   private ImageState imageState() {
      return new ImageState(width, height, pixels());
   }

   /**
    * Returns the pixel at the given coordinates.
    *
    * @param x the x coordinate of the pixel to grab
    * @param y the y coordinate of the pixel to grab
    * @return the Pixel at the location
    */
   public Pixel pixel(int x, int y) {
      try {
         return new Pixel(x, y, awt.getRGB(x, y));
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new RuntimeException("Coord (" + x + ", " + y + ") is out of bounds; size is (" + width + "," + height + ")");
      }
   }

   /**
    * Returns the pixel at the given coordinate.
    *
    * @param p the pixel as an integer tuple
    * @return the pixel
    */
   public Pixel pixel(Point p) {
      return pixel(p.x, p.y);
   }

   /**
    * Returns an array of every point in the image, useful if you want to be able to
    * iterate over all the coordinates.
    * <p>
    * If you want the actual pixel values of every point then use pixels().
    */
   public Point[] points() {
      Point[] points = new Point[width * height];
      int k = 0;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            points[k++] = new Point(x, y);
         }
      }
      return points;
   }

   /**
    * Returns the number of pixels in the image.
    *
    * @return the number of pixels
    */
   public int count() {
      return width * height;
   }

   /**
    * Executes the given side effecting function on each pixel.
    *
    * @param fn a function that accepts a pixel
    */
   public void forEach(Consumer<Pixel> fn) {
      Arrays.stream(points()).forEach(p -> fn.accept(pixel(p)));
   }

   /**
    * @return the pixels in the row identified by the y coordinate. 0 indexed.
    */
   public Pixel[] row(int y) {
      return pixels(0, y, width, 1);
   }

   /**
    * @return the pixels for the column identified by the x co-ordinate. 0 indexed.
    */
   public Pixel[] col(int x) {
      return pixels(x, 0, 1, height);
   }

   /**
    * Returns true if a pixel with the given color exists
    *
    * @param color the pixel colour to look for.
    * @return true if there exists at least one pixel that has the given pixels color
    */
   public boolean contains(Color color) {
      return exists(p -> p.toARGBInt() == RGBColor.fromAwt(color).toARGBInt());
   }

   /**
    * Returns true if the predicate is true for at least one pixel on the image.
    *
    * @param p a predicate
    * @return true if p holds for at least one pixel
    */
   public boolean exists(Predicate<Pixel> p) {
      return Arrays.stream(pixels()).anyMatch(p);
   }

   /**
    * Returns the color at the given coordinates.
    *
    * @return the RGBColor value at the coords
    */
   public RGBColor color(int x, int y) {
      return pixel(x, y).toColor();
   }

   /**
    * Returns the ARGB components for the pixel at the given coordinates
    *
    * @param x the x coordinate of the pixel component to grab
    * @param y the y coordinate of the pixel component to grab
    * @return an array containing ARGB components in that order.
    */
   public int[] argb(int x, int y) {
      Pixel p = pixel(x, y);
      return new int[]{p.alpha(), p.red(), p.green(), p.blue()};
   }

   /**
    * Returns the ARGB components for all pixels in this image
    *
    * @return an array containing an array for each ARGB components in that order.
    */
   public int[][] argb() {
      return Arrays.stream(points()).map(p -> argb(p.x, p.y)).toArray(int[][]::new);
   }

   /**
    * Returns the ARGB components for all pixels in this image as packed ints.
    *
    * @return an array containing the packed ARGB int for each pixel.
    */
   public int[] argbints() {
      return Arrays.stream(points()).mapToInt(p -> color(p.x, p.y).toARGBInt()).toArray();
   }

   public int[] rgb(int x, int y) {
      Pixel p = pixel(x, y);
      return new int[]{p.red(), p.green(), p.blue()};
   }

   public int[][] rgb() {
      return Arrays.stream(points()).map(p -> rgb(p.x, p.y)).toArray(int[][]::new);
   }

   /**
    * Returns a rectangular region within the given boundaries as a single
    * dimensional array of Pixels.
    * <p>
    * Eg, pixels(10, 10, 30, 20) would result in an array of size 600 with
    * the first row of the region in indexes 0,..,29, second row 30,..,59 etc.
    *
    * @param x the start x coordinate
    * @param y the start y coordinate
    * @param w the width of the region
    * @param h the height of the region
    * @return an Array of pixels for the region
    */
   public Pixel[] pixels(int x, int y, int w, int h) {
      Pixel[] pixels = new Pixel[w * h];
      int k = 0;
      for (int y1 = y; y1 < y + h; y1++) {
         for (int x1 = x; x1 < x + w; x1++) {
            pixels[k++] = pixel(x1, y1);
         }
      }
      return pixels;
   }

   public Pixel[] patch(int x, int y, int patchWidth, int patchHeight) {
      Pixel[] px = pixels();
      Pixel[] patch = new Pixel[patchWidth * patchHeight];
      for (int i = y; i < y + patchHeight; y++) {
         System.arraycopy(px, offset(x, y), patch, offset(0, y), patchWidth);
      }
      return patch;
   }

   @Override
   public String toString() {
      return "Image [width=" + width + ", height=" + height + ", type=" + awt.getType() + "]";
   }

   /**
    * Uses linear interpolation to get a sub-pixel.
    * <p>
    * Legal values for `x` and `y` are in [0, width) and [0, height),
    * respectively.
    */
   public int subpixel(double x, double y) {
      return new LinearSubpixelInterpolator(this).subpixel(x, y);
   }

   /**
    * Returns all the patches of a given size in the image, assuming pixel
    * alignment (no subpixel extraction).
    * <p>
    * The patches are returned as an array of of pixel matrices arrays.
    */
   public Pixel[][] patches(int patchWidth, int patchHeight) {
      Pixel[][] patches = new Pixel[(height - patchHeight) * (width - patchWidth)][];
      int k = 0;
      for (int row = 0; row < height - patchHeight; row++) {
         for (int col = 0; col < width - patchWidth; col++) {
            patches[k] = patch(col, row, patchWidth, patchHeight);
         }
      }
      return patches;
   }

   public int offset(int x, int y) {
      return PixelTools.coordsToOffset(x, y, width);
   }

   /**
    * Returns a set of the distinct colours used in this image.
    *
    * @return the set of distinct Colors
    */
   public Set<RGBColor> colours() {
      return Arrays.stream(pixels()).map(Pixel::toColor).collect(Collectors.toSet());
   }

   /**
    * Counts the number of pixels with the given colour.
    *
    * @param color the colour to detect.
    * @return the number of pixels that matched the colour of the given pixel
    */
   public long count(Color color) {
      return count(p -> p.toColor().equals(RGBColor.fromAwt(color)));
   }

   /**
    * Counts the number of pixels that are true for the given predicate
    *
    * @param p a predicate
    * @return the number of pixels that evaluated true
    */
   public long count(Predicate<Pixel> p) {
      return Arrays.stream(pixels()).filter(p).count();
   }

   /**
    * Returns a new AWT BufferedImage from this image.
    *
    * @param type the type of buffered image to create, if not specified then defaults to the current image type
    * @return a new, non-shared, BufferedImage with the same data as this Image.
    */
   public BufferedImage toNewBufferedImage(int type) {
      BufferedImage target = new BufferedImage(width, height, type);
      Graphics2D g2 = (Graphics2D) target.getGraphics();
      g2.drawImage(awt, 0, 0, null);
      g2.dispose();
      return target;
   }

   /**
    * Returns a new AWTImage with the same dimensions and same AWT type.
    * The data is uninitialized.
    */
   public AwtImage empty() {
      return new AwtImage(new BufferedImage(width, height, awt.getType()));
   }

   // See this Stack Overflow question to see why this is implemented this way.
   // http://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
   @Override
   public int hashCode() {
      return imageState().hashCode();
   }

   private <T> boolean sameElements(Iterator<T> a, Iterator<T> b) {
      boolean same = true;
      while (a.hasNext()) {
         if (!a.next().equals(b.next())) {
            same = false;
            break;
         }
      }
      return same;
   }

   public boolean equals(Object other) {
      if (other instanceof AwtImage) {
         AwtImage that = (AwtImage) other;
         return this.width == that.width &&
            this.height == that.height &&
            sameElements(iterator(), ((AwtImage) other).iterator());
      }
      return false;
   }

   protected BufferedImage scale(int targetWidth, int targetHeight, Scale scale) {
      return scale.scale(awt, targetWidth, targetHeight);
   }

   protected BufferedImage fastScaleScrimage(int targetWidth, int targetHeight) {
      return scale(targetWidth, targetHeight, new ScrimageNearestNeighbourScale());
   }

   /**
    * Returns a new AWT BufferedImage scaled using nearest-neighbour.
    */
   protected BufferedImage fastScaleAwt(int targetWidth, int targetHeight) {
      return scale(targetWidth, targetHeight, new AwtNearestNeighbourScale());
   }

   /**
    * Returns a new AWT Image rotated with the given angle (in radians)
    */
   @SuppressWarnings("SuspiciousNameCombination")
   protected BufferedImage rotateByRadians(Radians angle) {
      BufferedImage target = new BufferedImage(height, width, awt.getType());
      Graphics2D g2 = (Graphics2D) target.getGraphics();
      final int offsetx, offsety;
      if (angle.value < 0) {
         offsetx = 0;
         offsety = width;
      } else if (angle.value > 0) {
         offsetx = height;
         offsety = 0;
      } else {
         offsetx = 0;
         offsety = 0;
      }
      g2.translate(offsetx, offsety);
      g2.rotate(angle.value);
      g2.drawImage(awt, 0, 0, null);
      g2.dispose();
      return target;
   }

   /**
    * Returns true if the given predicate holds for all pixels in the image.
    */
   public boolean forAll(Predicate<Pixel> predicate) {
      return Arrays.stream(pixels()).allMatch(predicate);
   }

   /**
    * Programatically returns the origin point of top left.
    */
   public Pixel topLeftPixel() {
      return pixel(0, 0);
   }

   public Pixel bottomLeftPixel() {
      return pixel(0, height - 1);
   }

   public Pixel topRightPixel() {
      return pixel(width - 1, 0);
   }

   public Pixel bottomRightPixel() {
      return pixel(width - 1, height - 1);
   }

   /**
    * Returns true if this image supports transparency/alpha in its underlying data model.
    */
   public boolean hasAlpha() {
      return awt().getColorModel().hasAlpha();
   }

   /**
    * Returns true if this image supports transparency/alpha in its underlying data model.
    */
   public boolean hasTransparency() {
      return awt().getColorModel().hasAlpha();
   }

   /**
    * Returns the average colour of all pixels in this image
    */
   public RGBColor average() {
      return Arrays.stream(pixels()).map(Pixel::toColor).reduce(com.sksamuel.scrimage.color.Color::average).get();
   }

   /**
    * Returns true if all the pixels on this image are a single color.
    *
    * @param color the color to test pixels against
    */
   public boolean isFilled(Color color) {
      return forAll(p -> p.argb == RGBColor.fromAwt(color).toARGBInt());
   }

   public Path output(ImageWriter writer, String path) throws IOException {
      return forWriter(writer).write(Paths.get(path));
   }

   public File output(ImageWriter writer, File file) throws IOException {
      return forWriter(writer).write(file);
   }

   public Path output(ImageWriter writer, Path path) throws IOException {
      return forWriter(writer).write(path);
   }

   public byte[] bytes(ImageWriter writer) throws IOException {
      return forWriter(writer).bytes();
   }

   public WriteContext forWriter(ImageWriter writer) {
      return new WriteContext(writer, this, null);
   }

   /**
    * Returns a byte array stream consisting of the pixels of this image written out using
    * the supplied writer.
    *
    * @deprecated pointless method that simply wraps bytes()
    */
   @Deprecated
   public ByteArrayInputStream stream(ImageWriter writer) throws IOException {
      return forWriter(writer).stream();
   }
}
