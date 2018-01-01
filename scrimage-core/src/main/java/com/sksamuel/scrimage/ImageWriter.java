package com.sksamuel.scrimage;

import java.io.IOException;
import java.io.OutputStream;

/**
  * Typeclass supporting writing of an Image to an array of bytes in a specified format, eg JPEG
  */
public interface ImageWriter {
  void write(Image image, OutputStream out) throws IOException;
}