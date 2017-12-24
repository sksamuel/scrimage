package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class NegationComposite extends BlenderComposite {
    public NegationComposite(double alpha) {
        super(BlendingMode.NEGATION, alpha);
    }
}
