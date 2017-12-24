package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.PipelineFilter;
import com.sksamuel.scrimage.RGBColor;

import java.util.Arrays;

public class NashvilleFilter extends PipelineFilter {

    public NashvilleFilter() {
        super(Arrays.asList(
                new BackgroundBlendFilter(),
                new HSBFilter(0, -0.2, 0.5),
                new GammaFilter(1.2),
                new ContrastFilter(1.6),
                new VignetteFilter(0.9, 1, 0.6, new RGBColor(255, 140, 0, 255).toAWT())
        ));
    }
}

