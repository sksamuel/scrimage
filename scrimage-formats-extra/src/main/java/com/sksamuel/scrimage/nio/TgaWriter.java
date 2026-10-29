package com.sksamuel.scrimage.nio;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TgaWriter extends TwelveMonkeysWriter {

  // The TwelveMonkeys TGA encoder writes TYPE_BYTE_BINARY as a TGA with pixel depth 1, which
  // the spec does not allow and no reader (including TwelveMonkeys' own: AssertionError
  // "Unsupported pixel depth: 1") can decode. It also writes TYPE_USHORT_565_RGB samples
  // verbatim into a 16-bit TGA, which the format defines as 1-5-5-5, so every colour is
  // misread on decode. Indexed, grayscale and TYPE_USHORT_555_RGB are encoded natively.
  private static final Set<Integer> UNSUPPORTED_TYPES = new HashSet<>(Arrays.asList(
    BufferedImage.TYPE_BYTE_BINARY,
    BufferedImage.TYPE_USHORT_565_RGB
  ));

  @Override
  public String format() {
    return "tga";
  }

  @Override
  protected Set<Integer> unsupportedTypes() {
    return UNSUPPORTED_TYPES;
  }
}
