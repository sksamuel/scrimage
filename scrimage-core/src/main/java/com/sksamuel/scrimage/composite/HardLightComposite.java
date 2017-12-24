package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class HardLightComposite extends BlenderComposite {
    public HardLightComposite(double alpha) {
        super(BlendingMode.HARD_LIGHT, alpha);
    }
}
