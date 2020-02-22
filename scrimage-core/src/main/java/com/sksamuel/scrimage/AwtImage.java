package com.sksamuel.scrimage;

import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.pixels.Pixel;
import com.sksamuel.scrimage.pixels.PixelFunction;
import com.sksamuel.scrimage.pixels.PixelPredicate;
import com.sksamuel.scrimage.pixels.PixelTools;
import com.sksamuel.scrimage.scaling.AwtNearestNeighbourScale;
import com.sksamuel.scrimage.scaling.Scale;
import com.sksamuel.scrimage.scaling.ScrimageNearestNeighbourScale;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Read only operations on a BufferedImage.
 * You can think of this as a pimped-immutable-BufferedImage.
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
     * Returns all the pixels for the image
     *
     * @return an array of pixels for this image
     */
    public Pixel[] pixels() {
        Pixel[] pixels = new Pixel[count()];
        Point[] points = points();
        for (int k = 0; k < points.length; k++) {
            pixels[k] = pixel(points[k]);
        }
        return pixels;
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
                Coordinate coord = PixelTools.offsetToCoordinate(k++, width);
                int rgb = awt.getRGB(coord.getX(), coord.getY());
                return new Pixel(rgb);
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
        return new Pixel(awt.getRGB(x, y));
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
     * Returns true if the predicate is true for all pixels in the image.
     *
     * @param predicate a predicate function that accepts 3 parameters - the x,y coordinate and the pixel at that coordinate
     * @return true if f holds for at least one pixel
     */
    public boolean forall(PixelPredicate predicate) {
        return Arrays.stream(points()).allMatch(p -> predicate.test(p.x, p.y, pixel(p)));
    }

    /**
     * Executes the given side effecting function on each pixel.
     *
     * @param fn a function that accepts 3 parameters - the x,y coordinate and the pixel at that coordinate
     */
    public void foreach(PixelFunction fn) {
        Arrays.stream(points()).forEach(p -> fn.apply(p.x, p.y, pixel(p)));
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
        return exists(p -> p.toARGBInt() == RGBColor.fromAwt(color).toPixel().toARGBInt());
    }

    /**
     * Returns true if the predicate is true for at least one pixel on the image.
     *
     * @param p a predicate
     * @return true if p holds for at least one pixel
     */
    public boolean exists(Predicate<Pixel> p) {
        return stream().anyMatch(p);
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

    public int[] rgb(int x, int y) {
        Pixel p = pixel(x, y);
        return new int[]{p.red(), p.green(), p.blue()};
    }

    public int[][] rgb() {
        return Arrays.stream(points()).map(p -> rgb(p.x, p.y)).toArray(int[][]::new);
    }

    /**
     * Returns a rectangular region within the given boundaries as a single
     * dimensional array of integers.
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
        return PixelTools.coordinateToOffset(x, y, width);
    }

    /**
     * Returns a set of the distinct colours used in this image.
     *
     * @return the set of distinct Colors
     */
    public Set<RGBColor> colours() {
        return stream().map(Pixel::toColor).collect(Collectors.toSet());
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
        return stream().filter(p).count();
    }

    public Stream<Pixel> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
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
     * Returns a new AWT Image scaled using nearest-neighbour.
     */
    protected BufferedImage fastScaleAwt(int targetWidth, int targetHeight) {
        return scale(targetWidth, targetHeight, new AwtNearestNeighbourScale());
    }

    /**
     * Returns a new AWT Image rotated with the given angle (in degrees)
     */
    protected BufferedImage rotate(double angle) {
        BufferedImage target = new BufferedImage(height, width, awt.getType());
        Graphics2D g2 = (Graphics2D) target.getGraphics();
        final int offsetx, offsety;
        if (angle < 0) {
            offsetx = 0;
            offsety = width;
        } else if (angle > 0) {
            offsetx = height;
            offsety = 0;
        } else {
            offsetx = 0;
            offsety = 0;
        }
        g2.translate(offsetx, offsety);
        g2.rotate(angle);
        g2.drawImage(awt, 0, 0, null);
        g2.dispose();
        return target;
    }
}
