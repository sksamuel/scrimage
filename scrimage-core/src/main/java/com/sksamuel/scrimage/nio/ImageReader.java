package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

public interface ImageReader {
   ImmutableImage read(InputStream input, Rectangle rectangle) throws IOException;
}
