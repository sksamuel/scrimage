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

public class DitherFilter extends BufferedOpFilter {

    private final int levels;

    public DitherFilter() {
        // jhlabs DitherFilter default is 6 levels.
        this(6);
    }

    /**
     * @param levels the number of dither levels per channel (jhlabs default 6).
     */
    public DitherFilter(int levels) {
        this.levels = levels;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.DitherFilter op = new thirdparty.jhlabs.image.DitherFilter();
        op.setLevels(levels);
        return op;
    }
}