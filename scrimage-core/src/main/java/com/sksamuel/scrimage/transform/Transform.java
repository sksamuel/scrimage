package com.sksamuel.scrimage.transform;

import com.sksamuel.scrimage.ImmutableImage;

import java.io.IOException;

public interface Transform {
   ImmutableImage apply(ImmutableImage input) throws IOException;
}
