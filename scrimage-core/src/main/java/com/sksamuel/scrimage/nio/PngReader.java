package com.sksamuel.scrimage.nio;

import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.pixels.PixelTools;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class PngReader implements ImageReader {

   @Override
   public ImmutableImage read(byte[] bytes, Rectangle rectangle) {

      if (!isPng(bytes))
         return null;

      ar.com.hjg.pngj.PngReader pngr = new ar.com.hjg.pngj.PngReader(new ByteArrayInputStream(bytes));

      int channels = pngr.imgInfo.channels;
      int bitDepth = pngr.imgInfo.bitDepth;
      int w = pngr.imgInfo.cols;
      int h = pngr.imgInfo.rows;

      int[] matrix = new int[w * h];

      if (pngr.imgInfo.indexed) {
         int[] pixels = null;
         for (int row = 0; row < h; row++) {
            ImageLineInt scanline = (ImageLineInt) pngr.readRow();
            if (bitDepth < 8)
               ImageLineHelper.scaleUp(scanline);
            pixels = ImageLineHelper.palette2rgb(scanline, pngr.getMetadata().getPLTE(), null, pixels);
            int rowOffset = row * w;
            for (int x = 0, k = 0; x < w; x++, k += 3) {
               matrix[rowOffset + x] = PixelTools.argb(255, pixels[k], pixels[k + 1], pixels[k + 2]);
            }
         }
      } else {
         for (int row = 0; row < h; row++) {
            int[] scanline = ((ImageLineInt) pngr.readRow()).getScanline();
            int rowOffset = row * w;
            switch (channels) {
               case 1:
                  for (int x = 0, k = 0; x < w; x++, k += channels) {
                     int v = scanline[k];
                     matrix[rowOffset + x] = PixelTools.argb(255, v, v, v); // greyscale no alpha
                  }
                  break;
               case 2:
                  for (int x = 0, k = 0; x < w; x++, k += channels) {
                     int v = scanline[k];
                     matrix[rowOffset + x] = PixelTools.argb(scanline[k + 1], v, v, v);
                  }
                  break;
               case 3:
                  for (int x = 0, k = 0; x < w; x++, k += channels) {
                     matrix[rowOffset + x] = PixelTools.argb(255, scanline[k], scanline[k + 1], scanline[k + 2]); // if no alpha then 255 is full opacity
                  }
                  break;
               case 4:
                  for (int x = 0, k = 0; x < w; x++, k += channels) {
                     matrix[rowOffset + x] = PixelTools.argb(scanline[k + 3], scanline[k], scanline[k + 1], scanline[k + 2]); // note: the png reader is in RGBA
                  }
                  break;
            }
         }
      }
      pngr.end();

      DataBufferInt buffer = new DataBufferInt(matrix, matrix.length);
      int[] bandMasks = new int[]{0xFF0000, 0xFF00, 0xFF, 0xFF000000};
      WritableRaster raster = Raster.createPackedRaster(buffer, w, h, w, bandMasks, null);

      ColorModel cm = ColorModel.getRGBdefault();
      BufferedImage bi = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);

      ImmutableImage image = ImmutableImage.wrapAwt(bi, ImageMetadata.empty);
      if (rectangle != null) {
         image = image.subimage(rectangle);
      }
      return image;
   }

   private boolean isPng(byte[] bytes) {
      if (bytes.length < 8)
         return false;
      byte[] expected = new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
      byte[] actual = new byte[8];
      System.arraycopy(bytes, 0, actual, 0, 8);
      return Arrays.equals(expected, actual);
   }

   @Override
   public String toString() {
      return "com.sksamuel.scrimage.nio.PngReader (delegates to ar.com.hjg.pngj)";
   }
}
