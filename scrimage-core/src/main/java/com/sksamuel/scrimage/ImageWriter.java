package com.sksamuel.scrimage;

import java.io.IOException;
import java.io.OutputStream;

/**
  * Interface supporting writing of an Image to an array of bytes in a specified format, eg JPEG
  */
public interface ImageWriter {
  void write(ImmutableImage image, OutputStream out) throws IOException;
}