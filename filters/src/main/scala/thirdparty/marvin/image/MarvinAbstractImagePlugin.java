package thirdparty.marvin.image;

public abstract class MarvinAbstractImagePlugin extends MarvinAbstractPlugin implements MarvinImagePlugin {

    private boolean valid;


    /**
     * Executes the algorithm.
     *
     * @param imgIn   input image.
     * @param imgOut  output image.
     * @param attrOut output attributes.
     */
    public void process(MarvinImage imgIn,
                        MarvinImage imgOut,
                        MarvinImageMask mask) {
        process(imgIn, imgOut, null, mask, false);
    }

    public void process(MarvinImage imgIn,
                        MarvinImage imgOut,
                        MarvinAttributes attrOut) {
        process(imgIn, imgOut, attrOut, MarvinImageMask.NULL_MASK, false);
    }

    /**
     * Executes the algorithm.
     *
     * @param imgIn  input image.
     * @param imgOut output image.
     */
    public void process(MarvinImage imgIn, MarvinImage imgOut) {
        process(imgIn, imgOut, null, MarvinImageMask.NULL_MASK, false);
    }
}