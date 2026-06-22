package thirdparty.marvin.image.edge;


import thirdparty.marvin.Convolution;
import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

/**
 * @author Gabriel Ambr�sio Archanjo
 */
public class Prewitt extends MarvinAbstractImagePlugin {

    private Convolution convolution = new Convolution();

    // Definitions
    double[][] matrixPrewittX = new double[][]{
            {1, 0, -1},
            {1, 0, -1},
            {1, 0, -1}
    };

    double[][] matrixPrewittY = new double[][]{
            {1, 1, 1},
            {0, 0, 0},
            {-1, -1, -1}
    };

    public void process(
            MarvinImage imageIn,
            MarvinImage imageOut,
            MarvinAttributes attrOut,
            MarvinImageMask mask,
            boolean previewMode) {

        // Start from a blank canvas: the convolution passes accumulate onto
        // imageOut, which MarvinFilter seeds as a copy of the input.
        Convolution.clearRGB(imageOut);

        convolution.matrix = matrixPrewittX;
        convolution.process(imageIn, imageOut, null, mask, previewMode);

        convolution.matrix = matrixPrewittY;
        convolution.process(imageIn, imageOut, null, mask, previewMode);
    }
}
