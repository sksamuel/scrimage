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

public class ContrastFilter extends BufferedOpFilter {

    private final double contrast;

    public ContrastFilter(double contrast) {
        this.contrast = contrast;
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.ContrastFilter op = new thirdparty.jhlabs.image.ContrastFilter();
        op.setBrightness(1.0f);
        op.setContrast((float) contrast);
        return op;
    }
}
