package thirdparty.marvin.image;

public interface MarvinImagePlugin extends MarvinPlugin {

    /**
     * Executes the algorithm.
     *
     * @param imgIn       input image.
     * @param imgOut      output image.
     * @param attrOut     output attributes.
     * @param mask        mask containing what pixels should be considered.
     * @param previewMode it is or isn�t on preview mode.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut,
            MarvinAttributes attrOut,
            MarvinImageMask mask,
            boolean previewMode
    );

    /**
     * Executes the algorithm.
     *
     * @param imgIn   input image.
     * @param imgOut  output image.
     * @param attrOut output attributes.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut,
            MarvinImageMask mask
    );

    public void process
            (
                    MarvinImage imgIn,
                    MarvinImage imgOut,
                    MarvinAttributes attrOut
            );

    /**
     * Executes the algorithm.
     *
     * @param imgIn  input image.
     * @param imgOut output image.
     */
    public void process
    (
            MarvinImage imgIn,
            MarvinImage imgOut
    );
}