package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class ColorDodgeComposite extends BlenderComposite {
    public ColorDodgeComposite(double alpha) {
        super(BlendingMode.COLOR_DODGE, alpha);
    }
}
