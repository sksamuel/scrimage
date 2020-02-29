package com.sksamuel.scrimage.nio;

public class ByteArrayImageSource implements ImageSource {

   private final byte[] bytes;

   public ByteArrayImageSource(byte[] bytes) {
      this.bytes = bytes;
   }

   @Override
   public byte[] read() {
      return bytes;
   }
}
