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
import java.awt.image.ColorModel;

public final class MiscComposite implements Composite {

    public final static int BLEND = 0;
    public final static int ADD = 1;
    public final static int SUBTRACT = 2;

    public final static int STENCIL = 23;
    public final static int SILHOUETTE = 24;

    private static final int MIN_RULE = BLEND;
    private static final int MAX_RULE = SILHOUETTE;

    protected float extraAlpha;
    protected int rule;

    private MiscComposite(int rule, float alpha) {
        if (alpha < 0.0f || alpha > 1.0f)
            throw new IllegalArgumentException("alpha value out of range");
        if (rule < MIN_RULE || rule > MAX_RULE)
            throw new IllegalArgumentException("unknown composite rule");
        this.rule = rule;
        this.extraAlpha = alpha;
    }

    public static Composite getInstance(int rule, float alpha) {
        switch (rule) {
            case MiscComposite.BLEND:
                return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            case MiscComposite.ADD:
                return new AddComposite(alpha);
            case MiscComposite.SUBTRACT:
                return new SubtractComposite(alpha);
            case MiscComposite.STENCIL:
                return AlphaComposite.getInstance(AlphaComposite.DST_IN, alpha);
            case MiscComposite.SILHOUETTE:
                return AlphaComposite.getInstance(AlphaComposite.DST_OUT, alpha);
        }
        return new MiscComposite(rule, alpha);
    }

    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new MiscCompositeContext(rule, extraAlpha, srcColorModel, dstColorModel);
    }

    public int hashCode() {
        return (Float.floatToIntBits(extraAlpha) * 31 + rule);
    }

    public boolean equals(Object o) {
        if (!(o instanceof MiscComposite))
            return false;
        MiscComposite c = (MiscComposite) o;

        if (rule != c.rule)
            return false;
        if (extraAlpha != c.extraAlpha)
            return false;
        return true;
    }

}
