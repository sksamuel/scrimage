package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class ColorComposite extends BlenderComposite {
    public ColorComposite(double alpha) {
        super(BlendingMode.COLOR, alpha);
    }
}
