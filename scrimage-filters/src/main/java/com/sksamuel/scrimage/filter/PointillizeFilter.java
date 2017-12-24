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

import com.sksamuel.scrimage.BufferedOpFilter;

import java.awt.image.BufferedImageOp;

public class PointillizeFilter extends BufferedOpFilter {

    private final float angle;
    private final int scale;
    private final float edgeThickness;
    private final int edgeColor;
    private final boolean fadeEdges;
    private final float fuzziness;
    private final PointillizeGridType gridType;

    public PointillizeFilter(float angle, int scale, float edgeThickness, int edgeColor, boolean fadeEdges, float fuzziness, PointillizeGridType gridType) {
        this.angle = angle;
        this.scale = scale;
        this.edgeThickness = edgeThickness;
        this.edgeColor = edgeColor;
        this.fadeEdges = fadeEdges;
        this.fuzziness = fuzziness;
        this.gridType = gridType;
    }

    public PointillizeFilter(PointillizeGridType gridType) {
        this(0.0f, 6, 0.4f, 0xff000000, false, 0.1f, gridType);
    }

    @Override
    public BufferedImageOp op() {
        thirdparty.jhlabs.image.PointillizeFilter op = new thirdparty.jhlabs.image.PointillizeFilter();
        op.setAngle(angle);
        op.setScale(scale);
        op.setEdgeThickness(edgeThickness);
        op.setEdgeColor(edgeColor);
        op.setFadeEdges(fadeEdges);
        op.setFuzziness(fuzziness);
        switch (gridType) {
            case Random:
                op.setGridType(thirdparty.jhlabs.image.CellularFilter.RANDOM);
                break;
            case Square:
                op.setGridType(thirdparty.jhlabs.image.CellularFilter.SQUARE);
                break;
            case Hexagonal:
                op.setGridType(thirdparty.jhlabs.image.CellularFilter.HEXAGONAL);
                break;
            case Octangal:
                op.setGridType(thirdparty.jhlabs.image.CellularFilter.OCTAGONAL);
                break;
            case Triangular:
                op.setGridType(thirdparty.jhlabs.image.CellularFilter.TRIANGULAR);
                break;
        }
        return op;
    }
}