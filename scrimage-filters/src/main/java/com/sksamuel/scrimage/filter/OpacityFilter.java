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

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;
import com.sksamuel.scrimage.pixels.Pixel;

public class OpacityFilter implements Filter {

   private final float amount;

   public OpacityFilter(float amount) {
      this.amount = amount;
   }

   public void apply(ImmutableImage image) {
      image.mapInPlace(p -> {
         int _r = (int) (p.red() + (255 - p.red()) * amount);
         int _g = (int) (p.green() + (255 - p.green()) * amount);
         int _b = (int) (p.blue() + (255 - p.blue()) * amount);
         return new RGBColor(_r, _g, _b, p.alpha()).awt();
      });
   }
}
