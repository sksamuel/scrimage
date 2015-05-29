package thirdparty.pngtastic;

import java.util.List;

/**
 * Apply PNG interlacing and deinterlacing
 *
 * @author rayvanderborght
 */
public interface PngInterlaceHandler {

  /**
   * Do png deinterlacing on the given data
   *
   * @param width             The image width
   * @param height            The image height
   * @param sampleBitCount    The number of bits per sample
   * @param inflatedImageData The uncompressed image data, in interlaced form
   * @return A list of scanlines, each row represented as a byte array
   */
  List<byte[]> deInterlace(int width, int height, int sampleBitCount, byte[] inflatedImageData);

}