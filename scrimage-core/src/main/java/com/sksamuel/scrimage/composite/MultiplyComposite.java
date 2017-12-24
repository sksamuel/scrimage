package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class MultiplyComposite extends BlenderComposite {
    public MultiplyComposite(double alpha) {
        super(BlendingMode.MULTIPLY, alpha);
    }
}
