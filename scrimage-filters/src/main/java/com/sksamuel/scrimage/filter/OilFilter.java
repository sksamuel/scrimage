/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage.filter;

import java.awt.image.BufferedImageOp;

public class OilFilter extends BufferedOpFilter {

    private final int range;
    private final int levels;

    public OilFilter(int range, int levels) {
        if (range < 0)
            throw new IllegalArgumentException("range must be >= 0, got " + range);
        // The jhlabs implementation allocates histograms of size `levels`
        // and reads `rTotal[0] / rHistogram[0]` after the bucketing loop —
        // so `levels < 1` crashes with NegativeArraySizeException (negative)
        // or ArrayIndexOutOfBoundsException (zero). Buckets are computed
        // as `r * levels / 256`, so levels above 256 wastes memory without
        // adding fidelity but is technically valid; cap defensively anyway.
        if (levels < 1 || levels > 256)
            throw new IllegalArgumentException("levels must be in [1, 256], got " + levels);
        this.range = range;
        this.levels = levels;
    }

    public OilFilter() {
        this(3, 256);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.OilFilter op = new thirdparty.jhlabs.image.OilFilter();
        op.setRange(range);
        op.setLevels(levels);
        return op;
    }
}

