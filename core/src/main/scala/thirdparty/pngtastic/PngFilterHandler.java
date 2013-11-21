package thirdparty.pngtastic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Apply PNG filtering and defiltering
 *
 * @author rayvanderborght
 */
public interface PngFilterHandler {

  /**
   * Apply the given filter type to the scanlines provided.
   */
  void applyFiltering(PngFilterType filterType, List<byte[]> scanlines, int sampleBitCount);

  /**
   * Apply adaptive filtering as described in the png spec.
   */
  void applyAdaptiveFiltering(byte[] inflatedImageData,
                              List<byte[]> scanlines,
                              Map<PngFilterType, List<byte[]>> filteredScanLines,
                              int sampleSize) throws IOException;

  /**
   * Do filtering as described in the png spec:
   * The scanline starts with a filter type byte, then continues with the image data.
   */
  void filter(byte[] line, byte[] previousLine, int sampleBitCount) throws PngException;

  /**
   * Do the opposite of PNG filtering:
   *
   * @see #filter(byte[], byte[], int)
   */
  void deFilter(byte[] line, byte[] previousLine, int sampleBitCount) throws PngException;
}