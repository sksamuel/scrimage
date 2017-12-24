package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class AverageComposite extends BlenderComposite {
    public AverageComposite(double alpha) {
        super(BlendingMode.AVERAGE, alpha);
    }
}
