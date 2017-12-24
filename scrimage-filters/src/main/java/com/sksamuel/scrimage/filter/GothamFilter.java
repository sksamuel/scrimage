package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.PipelineFilter;

import java.util.Arrays;

public class GothamFilter extends PipelineFilter {

    public GothamFilter() {
        super(Arrays.asList(
                new HSBFilter(0, -0.85, 0.2),
                new ColorizeFilter(34, 43, 109, 25),
                new GammaFilter(0.5),
                new ContrastFilter(1.4)
        ));
    }
}