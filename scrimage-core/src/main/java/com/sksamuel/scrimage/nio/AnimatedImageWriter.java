package com.sksamuel.scrimage.nio;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface supporting writing of an Image to an array of bytes in a gif
 */
public interface AnimatedImageWriter {
   void write(AnimatedGif gif, OutputStream out) throws IOException;
}
