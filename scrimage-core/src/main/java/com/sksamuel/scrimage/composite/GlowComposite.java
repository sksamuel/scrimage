package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class GlowComposite extends BlenderComposite {
    public GlowComposite(double alpha) {
        super(BlendingMode.GLOW, alpha);
    }
}
