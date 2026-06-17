package com.sksamuel.scrimage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnsupportedFormatException extends IOException {

   private final List<Throwable> errors;

   public UnsupportedFormatException(List<Throwable> errors) {
      super(
         "Image parsing failed, could not determine format from provided bytes. Tried the following ImageReader implementations:\n" +
            errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n"))
      );
      this.errors = errors;
   }

   public UnsupportedFormatException() {
      this(new ArrayList<>());
   }

   public List<Throwable> getErrors() {
      return errors;
   }
}
