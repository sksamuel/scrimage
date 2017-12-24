package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class RedComposite extends BlenderComposite {
    public RedComposite(double alpha) {
        super(BlendingMode.RED, alpha);
    }
}