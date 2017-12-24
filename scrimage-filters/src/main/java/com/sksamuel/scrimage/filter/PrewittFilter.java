package com.sksamuel.scrimage.filter;

import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.edge.Prewitt;

public class PrewittFilter extends MarvinFilter {
    @Override
    public MarvinAbstractImagePlugin plugin() {
        return new Prewitt();
    }

}
