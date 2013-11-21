package thirdparty.pngtastic;

/**
 * Exception type for pngtastic code
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class PngException extends Exception {

  public PngException() {
  }

  public PngException(String message) {
    super(message);
  }

  public PngException(String message, Throwable cause) {
    super(message, cause);
  }
}
