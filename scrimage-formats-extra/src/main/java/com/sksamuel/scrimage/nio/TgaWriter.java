package com.sksamuel.scrimage.nio;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;

public class TgaWriter extends TwelveMonkeysWriter {

  // The TwelveMonkeys TGA encoder writes TYPE_BYTE_BINARY as a TGA with pixel depth 1, which
  // the spec does not allow and no reader (including TwelveMonkeys' own: AssertionError
  // "Unsupported pixel depth: 1") can decode. Indexed, grayscale and 16-bit RGB are encoded
  // natively.
  private static final Set<Integer> UNSUPPORTED_TYPES = Collections.singleton(BufferedImage.TYPE_BYTE_BINARY);

  @Override
  public String format() {
    return "tga";
  }

  @Override
  protected Set<Integer> unsupportedTypes() {
    return UNSUPPORTED_TYPES;
  }
}
