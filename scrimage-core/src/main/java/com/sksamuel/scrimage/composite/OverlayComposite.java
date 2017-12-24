package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class OverlayComposite extends BlenderComposite {
    public OverlayComposite(double alpha) {
        super(BlendingMode.OVERLAY, alpha);
    }
}
