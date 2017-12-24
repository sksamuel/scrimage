package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class HueComposite extends BlenderComposite {
    public HueComposite(double alpha) {
        super(BlendingMode.HUE, alpha);
    }
}
