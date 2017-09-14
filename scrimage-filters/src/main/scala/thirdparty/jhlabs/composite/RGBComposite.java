/*
Copyright 2006 Jerry Huxtable

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

package thirdparty.jhlabs.composite;

import java.awt.*;

public abstract class RGBComposite implements Composite {

    protected float extraAlpha;

    public RGBComposite() {
        this(1.0f);
    }

    public RGBComposite(float alpha) {
        if (alpha < 0.0f || alpha > 1.0f)
            throw new IllegalArgumentException("RGBComposite: alpha must be between 0 and 1");
        this.extraAlpha = alpha;
    }

    public int hashCode() {
        return Float.floatToIntBits(extraAlpha);
    }

    public boolean equals(Object o) {
        if (!(o instanceof RGBComposite))
            return false;
        RGBComposite c = (RGBComposite) o;
        return extraAlpha == c.extraAlpha;
    }


}
