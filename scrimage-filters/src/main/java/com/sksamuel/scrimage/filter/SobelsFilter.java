package com.sksamuel.scrimage.filter;

import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.edge.Sobel;

public class SobelsFilter extends MarvinFilter {
    @Override
    public MarvinAbstractImagePlugin plugin() {
        return new Sobel();
    }
}
