package com.sksamuel.scrimage.transform;

import com.sksamuel.scrimage.ImmutableImage;

import java.io.IOException;

/**
 * Creates a new image by applying a transformation, such as resizing, cropping, or overlaying.
 */
public interface Transform {
   ImmutableImage apply(ImmutableImage input) throws IOException;
}
