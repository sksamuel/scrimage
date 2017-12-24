package com.sksamuel.scrimage.composite;

import thirdparty.romainguy.BlendingMode;

public class ReflectComposite extends BlenderComposite {
    public ReflectComposite(double alpha) {
        super(BlendingMode.REFLECT, alpha);
    }
}
