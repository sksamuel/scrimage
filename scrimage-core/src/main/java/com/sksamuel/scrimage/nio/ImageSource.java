package com.sksamuel.scrimage.nio;

import java.io.IOException;
import java.io.InputStream;

public interface ImageSource {
   InputStream open() throws IOException;
}
