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
import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.Pixel$;

public class GrayscaleFilter implements Filter {

    public void apply(Image image) {
        image.mapInPlace((x, y, p) -> {
            double red = 0.21 * p.red();
            double green = 0.71 * p.green();
            double blue = 0.07 * p.blue();
            int gray = (int) (red + green + blue);
            return Pixel$.MODULE$.apply(gray, gray, gray, p.alpha());
        });
  }
}