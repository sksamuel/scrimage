package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class ColorBurnComposite extends BlenderComposite {
    public ColorBurnComposite(double alpha) {
        super(BlendingMode.COLOR_BURN, alpha);
    }
}
