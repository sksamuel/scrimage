package com.sksamuel.scrimage.pixels;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Bulk packed-ARGB reads and writes that are bit-identical to
 * {@link BufferedImage#getRGB(int, int, int, int, int[], int, int)} and
 * {@link BufferedImage#setRGB(int, int, int, int, int[], int, int)}, but much faster
 * for the common standard image types.
 * <p>
 * The JDK's bulk getRGB/setRGB convert one pixel at a time through the ColorModel.
 * For TYPE_INT_ARGB, TYPE_INT_RGB, TYPE_3BYTE_BGR (what JPEGs load as) and
 * TYPE_4BYTE_ABGR (PNGs with alpha) that conversion is a fixed bit shuffle, so here
 * the raster's data elements are copied in bulk (row-wise arraycopy inside the raster)
 * and shuffled directly. Raster.get/setDataElements honour sub-raster offsets and
 * scanline strides, and do not "steal" the DataBuffer, so the image stays managed.
 * Every other type (premultiplied, indexed, grey, 16-bit, custom, ...) falls back to the
 * JDK getRGB/setRGB, so behaviour there is unchanged.
 */
public final class FastRgb {

   private FastRgb() {
   }

   /**
    * Equivalent to {@code img.getRGB(x, y, w, h, null, 0, w)}.
    */
   public static int[] getRGB(BufferedImage img, int x, int y, int w, int h) {
      if (w <= 0 || h <= 0) return img.getRGB(x, y, w, h, null, 0, w);
      WritableRaster raster = img.getRaster();
      switch (img.getType()) {
         case BufferedImage.TYPE_INT_ARGB:
            return (int[]) raster.getDataElements(x, y, w, h, new int[w * h]);
         case BufferedImage.TYPE_INT_RGB: {
            int[] out = (int[]) raster.getDataElements(x, y, w, h, new int[w * h]);
            for (int i = 0; i < out.length; i++) out[i] |= 0xFF000000;
            return out;
         }
         case BufferedImage.TYPE_3BYTE_BGR: {
            // data elements are in band order: r, g, b
            int[] out = new int[w * h];
            byte[] row = new byte[w * 3];
            for (int j = 0; j < h; j++) {
               raster.getDataElements(x, y + j, w, 1, row);
               int o = j * w;
               for (int i = 0, k = 0; i < w; i++, k += 3) {
                  out[o + i] = 0xFF000000 | ((row[k] & 0xFF) << 16) | ((row[k + 1] & 0xFF) << 8) | (row[k + 2] & 0xFF);
               }
            }
            return out;
         }
         case BufferedImage.TYPE_4BYTE_ABGR: {
            // data elements are in band order: r, g, b, a
            int[] out = new int[w * h];
            byte[] row = new byte[w * 4];
            for (int j = 0; j < h; j++) {
               raster.getDataElements(x, y + j, w, 1, row);
               int o = j * w;
               for (int i = 0, k = 0; i < w; i++, k += 4) {
                  out[o + i] = ((row[k + 3] & 0xFF) << 24) | ((row[k] & 0xFF) << 16) | ((row[k + 1] & 0xFF) << 8) | (row[k + 2] & 0xFF);
               }
            }
            return out;
         }
         default:
            return img.getRGB(x, y, w, h, null, 0, w);
      }
   }

   /**
    * Equivalent to {@code img.getRGB(0, 0, width, height, null, 0, width)}.
    */
   public static int[] getRGB(BufferedImage img) {
      return getRGB(img, 0, 0, img.getWidth(), img.getHeight());
   }

   /**
    * Equivalent to {@code img.setRGB(x, y, w, h, argb, 0, w)}, including the raw
    * values stored in the raster.
    */
   public static void setRGB(BufferedImage img, int x, int y, int w, int h, int[] argb) {
      if (w <= 0 || h <= 0 || argb.length < w * h) {
         img.setRGB(x, y, w, h, argb, 0, w);
         return;
      }
      WritableRaster raster = img.getRaster();
      switch (img.getType()) {
         case BufferedImage.TYPE_INT_ARGB:
            raster.setDataElements(x, y, w, h, argb);
            return;
         case BufferedImage.TYPE_INT_RGB: {
            // setRGB stores the 24 colour bits only
            int[] row = new int[w];
            for (int j = 0; j < h; j++) {
               int o = j * w;
               for (int i = 0; i < w; i++) row[i] = argb[o + i] & 0x00FFFFFF;
               raster.setDataElements(x, y + j, w, 1, row);
            }
            return;
         }
         case BufferedImage.TYPE_3BYTE_BGR: {
            byte[] row = new byte[w * 3];
            for (int j = 0; j < h; j++) {
               int o = j * w;
               for (int i = 0, k = 0; i < w; i++, k += 3) {
                  int p = argb[o + i];
                  row[k] = (byte) (p >> 16);
                  row[k + 1] = (byte) (p >> 8);
                  row[k + 2] = (byte) p;
               }
               raster.setDataElements(x, y + j, w, 1, row);
            }
            return;
         }
         case BufferedImage.TYPE_4BYTE_ABGR: {
            byte[] row = new byte[w * 4];
            for (int j = 0; j < h; j++) {
               int o = j * w;
               for (int i = 0, k = 0; i < w; i++, k += 4) {
                  int p = argb[o + i];
                  row[k] = (byte) (p >> 16);
                  row[k + 1] = (byte) (p >> 8);
                  row[k + 2] = (byte) p;
                  row[k + 3] = (byte) (p >>> 24);
               }
               raster.setDataElements(x, y + j, w, 1, row);
            }
            return;
         }
         default:
            img.setRGB(x, y, w, h, argb, 0, w);
      }
   }

   /**
    * Equivalent to {@code img.setRGB(0, 0, width, height, argb, 0, width)}.
    */
   public static void setRGB(BufferedImage img, int[] argb) {
      setRGB(img, 0, 0, img.getWidth(), img.getHeight(), argb);
   }
}
