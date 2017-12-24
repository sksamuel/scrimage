package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class ScreenComposite extends BlenderComposite {
    public ScreenComposite(double alpha) {
        super(BlendingMode.SCREEN, alpha);
    }
}
