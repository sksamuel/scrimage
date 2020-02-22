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
import thirdparty.marvin.image.MarvinAbstractImagePlugin;
import thirdparty.marvin.image.MarvinAttributes;
import thirdparty.marvin.image.MarvinImage;
import thirdparty.marvin.image.MarvinImageMask;

/**
 * Implementation of Filter that provides filtering through delegation
 * to a plugin from the Marvin framework. The plugins are modified
 * so that the dependancy on the marvin gui is removed.
 *
 */
abstract class MarvinFilter implements Filter {

  public abstract MarvinAbstractImagePlugin plugin();

  public void apply(ImmutableImage image) {

    MarvinImage input = new MarvinImage(image.awt());
    MarvinImage output = input.clone();

    plugin().process(input, output, new MarvinAttributes(), MarvinImageMask.NULL_MASK, false);

    input.setIntColorArray(output.getIntColorArray());
    input.update();
  }
}
