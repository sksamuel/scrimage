package com.sksamuel.scrimage;

import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.pixels.Pixel;
import com.sksamuel.scrimage.pixels.PixelTools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Extends [AwtImage] with methods that operate on a [BufferedImage] by mutating the buffer.
 * All methods in this class should return void as they operate on the underlying image in place.
 * <p>
 * This class cannot contain methods that result in a changed canvas size, as there is no
 * way to mutate the size of a raster once created.
 */
public class MutableImage extends AwtImage {

   public MutableImage(BufferedImage awt) {
      super(awt);
   }

   /**
    * Maps the pixels of this image into another image by applying the given function to each pixel.
    * <p>
    * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
    * being transformed and p is the pixel at that location.
    *
    * @param mapper the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
    */
   public void mapInPlace(Function<Pixel, Color> mapper) {
      int[] argb = awt().getRGB(0, 0, width, height, null, 0, width);
      int i = 0;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            Color c = mapper.apply(new Pixel(x, y, argb[i]));
            argb[i++] = c.getRGB();
         }
      }
      awt().setRGB(0, 0, width, height, argb, 0, width);
   }

   public void replaceTransparencyInPlace(java.awt.Color color) {
      int[] argb = awt().getRGB(0, 0, width, height, null, 0, width);
      int i = 0;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            Pixel pixel = new Pixel(x, y, argb[i]);
            argb[i++] = PixelTools.replaceTransparencyWithColor(pixel, color).toARGBInt();
         }
      }
      awt().setRGB(0, 0, width, height, argb, 0, width);
   }

   /**
    * Fills all pixels the given color on the existing image.
    */
   public void fillInPlace(Color color) {
      WritableRaster raster = awt().getRaster();
      Object colorElements = awt().getColorModel().getDataElements(RGBColor.fromAwt(color).toARGBInt(), null);
      Arrays.stream(points()).forEach(point -> raster.setDataElements(point.x, point.y, colorElements));
   }

   /**
    * Applies the given image over the current buffer.
    */
   public void overlayInPlace(BufferedImage overlay, int x, int y) {
      Graphics2D g2 = (Graphics2D) awt().getGraphics();
      g2.drawImage(overlay, x, y, null);
      g2.dispose();
   }

   public void setColor(int offset, com.sksamuel.scrimage.color.Color color) {
      Point point = PixelTools.offsetToPoint(offset, width);
      awt().setRGB(point.x, point.y, color.toRGB().toARGBInt());
   }

   public void setColor(int x, int y, com.sksamuel.scrimage.color.Color color) {
      awt().setRGB(x, y, color.toRGB().toARGBInt());
   }

   public void setPixel(Pixel pixel) {
      awt().setRGB(pixel.x, pixel.y, pixel.toARGBInt());
   }

   /**
    * Mutates this image by scaling all pixel values by the given factor (brightness in other words).
    */
   public void rescaleInPlace(double factor) {
      RescaleOp rescale = new RescaleOp((float) factor, 0f,
         new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
      rescale.filter(awt(), awt());
   }

   public void contrastInPlace(double factor) {
      int[] argb = awt().getRGB(0, 0, width, height, null, 0, width);
      int i = 0;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            Pixel pixel = new Pixel(x, y, argb[i]);
            int r = PixelTools.truncate((factor * (pixel.red() - 128)) + 128);
            int g = PixelTools.truncate((factor * (pixel.green() - 128)) + 128);
            int b = PixelTools.truncate((factor * (pixel.blue() - 128)) + 128);
            argb[i++] = new Pixel(x, y, r, g, b, pixel.alpha()).toARGBInt();
         }
      }
      awt().setRGB(0, 0, width, height, argb, 0, width);
   }
}
