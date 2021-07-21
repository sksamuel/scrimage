package com.sksamuel.scrimage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageParseException extends IOException {

   private final List<Throwable> errors;

   public ImageParseException() {
      errors = new ArrayList<>();
   }

   public ImageParseException(List<Throwable> errors) {
      super("Image parsing failed. Tried the following ImageReader implementations:\n" + errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n")));
      this.errors = errors;
   }

   public List<Throwable> getErrors() {
      return errors;
   }
}
