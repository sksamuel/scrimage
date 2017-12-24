package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.PipelineFilter;
import com.sksamuel.scrimage.RGBColor;

import java.util.Arrays;

public class NashvilleFilter extends PipelineFilter {

    public NashvilleFilter() {
        super(Arrays.asList(
                new BackgroundBlendFilter(),
                new HSBFilter(0, -0.2f, 0.5f),
                new GammaFilter(1.2),
                new ContrastFilter(1.6),
                new VignetteFilter(0.9f, 1, 0.6f, new RGBColor(255, 140, 0, 255).toAWT())
        ));
    }
}

