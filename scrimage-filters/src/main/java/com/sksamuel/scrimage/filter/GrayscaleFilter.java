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

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.RGBColor;

public class GrayscaleFilter implements Filter {

   public void apply(ImmutableImage image) {
      image.mapInPlace((p) -> {
         int gray = (int) Math.round(0.2126 * p.red() + 0.7152 * p.green() + 0.0722 * p.blue());
         return new RGBColor(gray, gray, gray, p.alpha()).awt();
      });
   }
}
