package thirdparty.marvin.image.edge;

import thirdparty.marvin.Convolution;
import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

/**
 * @author Gabriel Ambr�sio Archanjo
 */
public class Roberts extends MarvinAbstractImagePlugin {

  private Convolution convolution = new Convolution();

  double[][] matrixRobertsX = new double[][]{
    {1, 0},
    {0, -1}
  };

  double[][] matrixRobertsY = new double[][]{
    {0, 1},
    {-1, 0}
  };

  public void process(MarvinImage imageIn,
                      MarvinImage imageOut,
                      MarvinAttributes attrOut,
                      MarvinImageMask mask,
                      boolean previewMode) {
    // Start from a blank canvas: the convolution passes accumulate onto
    // imageOut, which MarvinFilter seeds as a copy of the input.
    Convolution.clearRGB(imageOut);

    convolution.matrix = matrixRobertsX;
    convolution.process(imageIn, imageOut, null, mask, previewMode);

    convolution.matrix = matrixRobertsY;
    convolution.process(imageIn, imageOut, null, mask, previewMode);
  }
}
