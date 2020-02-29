package com.sksamuel.scrimage.nio;

import java.io.IOException;

public interface ImageSource {
   byte[] read() throws IOException;
}
