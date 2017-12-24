package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class SubtractComposite extends BlenderComposite {
    public SubtractComposite(double alpha) {
        super(BlendingMode.SUBTRACT, alpha);
    }
}
