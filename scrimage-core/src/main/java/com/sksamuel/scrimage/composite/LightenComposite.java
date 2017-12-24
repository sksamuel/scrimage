package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class LightenComposite extends BlenderComposite {
    public LightenComposite(double alpha) {
        super(BlendingMode.LIGHTEN, alpha);
    }
}
