package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class SaturationComposite extends BlenderComposite {
    public SaturationComposite(double alpha) {
        super(BlendingMode.SATURATION, alpha);
    }
}
