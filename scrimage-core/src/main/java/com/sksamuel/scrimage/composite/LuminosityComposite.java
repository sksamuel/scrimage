package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class LuminosityComposite extends BlenderComposite {
    public LuminosityComposite(double alpha) {
        super(BlendingMode.LUMINOSITY, alpha);
    }
}
