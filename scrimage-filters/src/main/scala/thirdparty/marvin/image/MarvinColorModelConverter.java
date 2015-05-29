
package thirdparty.marvin.image;

/**
 * Image color model conversions.
 *
 * @author Gabriel Ambrosio Archanjo
 * @version 09/02/11
 */
public class MarvinColorModelConverter {

    /**
     * Converts an image in RGB mode to BINARY mode
     *
     * @param img       image
     * @param threshold grays cale threshold
     * @return new MarvinImage instance in BINARY mode
     */
    public static MarvinImage rgbToBinary(MarvinImage img, int threshold) {
        MarvinImage resultImage = new MarvinImage(img.getWidth(), img.getHeight(), MarvinImage.COLOR_MODEL_BINARY);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int gray = (int) ((img.getIntComponent0(x, y) * 0.3) + (img.getIntComponent1(x, y) * 0.59) + (img.getIntComponent2(x, y) * 0.11));

                if (gray <= threshold) {
                    resultImage.setBinaryColor(x, y, true);
                } else {
                    resultImage.setBinaryColor(x, y, false);
                }
            }
        }
        return resultImage;
    }

    /**
     * Converts an image in BINARY mode to RGB mode
     *
     * @param img image
     * @return new MarvinImage instance in RGB mode
     */
    public static MarvinImage binaryToRgb(MarvinImage img) {
        MarvinImage resultImage = new MarvinImage(img.getWidth(), img.getHeight(), MarvinImage.COLOR_MODEL_RGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (img.getBinaryColor(x, y)) {
                    resultImage.setIntColor(x, y, 0, 0, 0);
                } else {
                    resultImage.setIntColor(x, y, 255, 255, 255);
                }
            }
        }
        return resultImage;
    }

    /**
     * Converts a boolean array containing the pixel data in BINARY mode to an
     * integer array with the pixel data in RGB mode.
     *
     * @param binaryArray pixel binary data
     * @return pixel integer data in RGB mode.
     */
    public static int[] binaryToRgb(boolean[] binaryArray) {
        int[] rgbArray = new int[binaryArray.length];

        for (int i = 0; i < binaryArray.length; i++) {
            if (binaryArray[i]) {
                rgbArray[i] = 0x00000000;
            } else {
                rgbArray[i] = 0x00FFFFFF;
            }
        }
        return rgbArray;
    }
}
