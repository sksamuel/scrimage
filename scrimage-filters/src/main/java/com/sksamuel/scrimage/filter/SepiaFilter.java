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

import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.color.Sepia;

public class SepiaFilter extends MarvinFilter {

    private final int intensity;

    /**
     * Creates a SepiaFilter with the default intensity of 20.
     */
    public SepiaFilter() {
        this(20);
    }

    /**
     * Creates a SepiaFilter with the given intensity, which is passed through to
     * the underlying Sepia plugin.
     *
     * @param intensity the sepia intensity
     */
    public SepiaFilter(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public MarvinAbstractImagePlugin plugin() {
        return new Sepia(intensity);
    }
}
