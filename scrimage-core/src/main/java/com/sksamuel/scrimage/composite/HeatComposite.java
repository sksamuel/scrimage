package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class HeatComposite extends BlenderComposite {
    public HeatComposite(double alpha) {
        super(BlendingMode.HEAT, alpha);
    }
}
