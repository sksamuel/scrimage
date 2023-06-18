package com.sksamuel.scrimage;

import com.sksamuel.scrimage.format.Format;

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
      super("Image parsing failed for unknown image format. Tried the following ImageReader implementations:\n" + errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n")));
      this.errors = errors;
   }

   public ImageParseException(List<Throwable> errors, Format format) {
      super("Image parsing failed for " + format + ". If the format is `webp` ensure you have a webp reader on your classpath, such as the `scrimage-webp` module. Tried the following ImageReader implementations:\n" + errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n")));
      this.errors = errors;
   }

   public List<Throwable> getErrors() {
      return errors;
   }
}
