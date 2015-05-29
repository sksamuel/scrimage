package thirdparty.pngtastic;

/**
 * Represents the image types available in PNG images
 * <pre>
 * Table 11.1 - Allowed combinations of colour type and bit depth
 * PNG image type			Colour type	Allowed bit depths	Interpretation
 * Greyscale				0			1, 2, 4, 8, 16		Each pixel is a greyscale sample
 * Truecolour				2			8, 16				Each pixel is an R,G,B triple
 * Indexed-colour			3			1, 2, 4, 8			Each pixel is a palette index; a PLTE chunk shall appear.
 * Greyscale with alpha		4			8, 16				Each pixel is a greyscale sample followed by an alpha sample.
 * Truecolour with alpha	6			8, 16				Each pixel is an R,G,B triple followed by an alpha sample.
 * </pre>
 *
 * @author rayvanderborght
 */
public enum PngImageType {

  GREYSCALE(0),
  TRUECOLOR(2),
  INDEXED_COLOR(3),
  GREYSCALE_ALPHA(4),
  TRUECOLOR_ALPHA(6);

  private int colorType;

  private PngImageType(int colorType) {
    this.colorType = colorType;
  }

  public static PngImageType forColorType(int colorType) {
    switch (colorType) {
      case 0:
        return PngImageType.GREYSCALE;

      case 2:
        return PngImageType.TRUECOLOR;

      case 3:
        return PngImageType.INDEXED_COLOR;

      case 4:
        return PngImageType.GREYSCALE_ALPHA;

      case 6:
        return PngImageType.TRUECOLOR_ALPHA;

      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * The number of channels for this color type.
   * For example truecolor is RGB and therefore has 3 channels.
   *
   * @return The number of channels for this image color type
   */
  public int channelCount() {
    switch (this.colorType) {
      case 0:
      case 3:
        return 1;

      case 4:
        return 2;

      case 2:
        return 3;

      case 6:
        return 4;

      default:
        throw new IllegalArgumentException();
    }
  }
}