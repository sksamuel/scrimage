package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class DifferenceComposite extends BlenderComposite {
    public DifferenceComposite(double alpha) {
        super(BlendingMode.DIFFERENCE, alpha);
    }
}
