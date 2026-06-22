
package thirdparty.marvin.image;

/**
 * Image color model conversions.
 *
 * @author Gabriel Ambrosio Archanjo
 * @version 09/02/11
 */
public class MarvinColorModelConverter {

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
