package com.sksamuel.scrimage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageParseException extends IOException {

   private final List<Throwable> errors;

   public ImageParseException() {
      errors = new ArrayList<>();
   }

   public ImageParseException(List<Throwable> errors) {
      super(errors.get(0));
      this.errors = errors;
   }

   public List<Throwable> getErrors() {
      return errors;
   }
}
