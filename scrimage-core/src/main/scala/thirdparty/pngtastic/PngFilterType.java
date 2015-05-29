package thirdparty.pngtastic;

/**
 * Represents the available PNG filter types.
 *
 * @author rayvanderborght
 * @see <a href="http://www.w3.org/TR/PNG/#9Filters">Filtering</a>
 */
public enum PngFilterType {

  ADAPTIVE(-1),  // NOTE: not a real filter type
  NONE(0),
  SUB(1),
  UP(2),
  AVERAGE(3),
  PAETH(4);

  private byte value;
  public byte getValue() {
    return this.value;
  }

  private PngFilterType(int i) {
    this.value = (byte) i;
  }

  public static PngFilterType forValue(byte value) {
    for (PngFilterType type : PngFilterType.values()) {
      if (type.getValue() == value)
        return type;
    }
    return NONE;
  }

  public static PngFilterType[] standardValues() {
    return new PngFilterType[]{NONE, SUB, UP, AVERAGE, PAETH};
  }
}
