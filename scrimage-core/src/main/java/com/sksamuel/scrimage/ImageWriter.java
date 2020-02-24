package com.sksamuel.scrimage;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface supporting writing of an Image to an array of bytes in a specified format, eg JPEG
 *
 * @see com.sksamuel.scrimage.nio.JpegWriter
 * @see com.sksamuel.scrimage.nio.PngWriter
 * @see com.sksamuel.scrimage.nio.GifWriter
 */
public interface ImageWriter {
   void write(AwtImage image, OutputStream out) throws IOException;
}
