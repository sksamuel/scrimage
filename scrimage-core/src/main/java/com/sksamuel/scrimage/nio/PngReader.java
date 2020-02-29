package com.sksamuel.scrimage.nio;

import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.metadata.ImageMetadata;

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
         for (int row = 0; row < h; row++) {
            ImageLineInt scanline = (ImageLineInt) pngr.readRow();
            if (bitDepth < 8)
               ImageLineHelper.scaleUp(scanline);
            int[] pixels = ImageLineHelper.palette2rgb(scanline, pngr.getMetadata().getPLTE(), null, null);
            int j = 0;
            int[] mapped = new int[pixels.length / 3];
            for (int k = 0; k < pixels.length; k = k + 3) {
               mapped[j++] = new RGBColor(pixels[k], pixels[k + 1], pixels[k + 2], 255).toARGBInt();
            }
            System.arraycopy(mapped, 0, matrix, row * w, w);
         }
      } else {
         for (int row = 0; row < h; row++) {
            int[] scanline = ((ImageLineInt) pngr.readRow()).getScanline();
            int j = 0;
            int[] mapped = new int[scanline.length / channels];
            for (int k = 0; k < scanline.length; k = k + channels) {

               switch (channels) {
                  case 1:
                     mapped[j++] = new RGBColor(scanline[k], scanline[k], scanline[k], 255).toARGBInt(); // greyscale no alpha
                     break;
                  case 2:
                     mapped[j++] = new RGBColor(scanline[k], scanline[k], scanline[k], scanline[k + 1]).toARGBInt();
                     break;
                  case 3:
                     mapped[j++] = new RGBColor(scanline[k], scanline[k + 1], scanline[k + 2], 255).toARGBInt(); // if no alpha then 255 is full opacity
                     break;
                  case 4:
                     mapped[j++] = new RGBColor(scanline[k], scanline[k + 1], scanline[k + 2], scanline[k + 3]).toARGBInt(); // note: the png reader is n RGBA
                     break;
               }
            }
            System.arraycopy(mapped, 0, matrix, row * w, w);
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
}
