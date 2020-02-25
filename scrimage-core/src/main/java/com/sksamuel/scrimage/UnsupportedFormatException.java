package com.sksamuel.scrimage;

import java.io.IOException;

public class UnsupportedFormatException extends IOException {
   public UnsupportedFormatException(String msg) {
      super(msg);
   }
}
