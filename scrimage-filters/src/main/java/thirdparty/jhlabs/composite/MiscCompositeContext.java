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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class MiscCompositeContext implements CompositeContext {

    private int rule;
    private float alpha;

    public MiscCompositeContext(int rule,
                                float alpha,
                                ColorModel srcColorModel,
                                ColorModel dstColorModel) {
        this.rule = rule;
        this.alpha = alpha;
    }

    @Override
    public void dispose() {
    }

    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        float a = 0, ac = 0;
        float alpha = this.alpha;
        int t;

        int[] srcPix = null;
        int[] dstPix = null;

        int x = dstOut.getMinX();
        int w = dstOut.getWidth();

        int y0 = dstOut.getMinY();
        int y1 = y0 + dstOut.getHeight();

        for (int y = y0; y < y1; y++) {
            srcPix = src.getPixels(x, y, w, 1, srcPix);
            dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
            int i = 0;
            int end = w * 4;

            while (i < end) {
                int sr = srcPix[i];
                int dir = dstPix[i];
                int sg = srcPix[i + 1];
                int dig = dstPix[i + 1];
                int sb = srcPix[i + 2];
                int dib = dstPix[i + 2];
                int sa = srcPix[i + 3];
                int dia = dstPix[i + 3];
                int dor, dog, dob, doa;

                switch (rule) {
                    case MiscComposite.ADD:
                    default:
                        dor = dir + sr;
                        if (dor > 255)
                            dor = 255;
                        dog = dig + sg;
                        if (dog > 255)
                            dog = 255;
                        dob = dib + sb;
                        if (dob > 255)
                            dob = 255;
                        break;

                    case MiscComposite.SUBTRACT:
                        dor = dir - sr;
                        if (dor < 0)
                            dor = 0;
                        dog = dig - sg;
                        if (dog < 0)
                            dog = 0;
                        dob = dib - sb;
                        if (dob < 0)
                            dob = 0;
                        break;

                }

                a = alpha * sa / 255f;
                ac = 1 - a;

                dstPix[i] = (int) (a * dor + ac * dir);
                dstPix[i + 1] = (int) (a * dog + ac * dig);
                dstPix[i + 2] = (int) (a * dob + ac * dib);
                dstPix[i + 3] = (int) (sa * alpha + dia * ac);
                i += 4;
            }
            dstOut.setPixels(x, y, w, 1, dstPix);

        }
    }
}
