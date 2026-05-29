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

import java.awt.Graphics2D;

/**
 * Repeats (tiles) the source image across the canvas. Given {@code columns} and
 * {@code rows}, the image is laid out as a grid of {@code columns} copies across
 * and {@code rows} copies down, each copy scaled to fit its cell. The output
 * keeps the original dimensions, so {@code RepeaterFilter(2, 3)} produces a 2x3
 * grid of shrunken copies of the original within the same frame.
 */
public class RepeaterFilter implements Filter {

   private final int columns;
   private final int rows;

   public RepeaterFilter(int columns, int rows) {
      if (columns < 1 || rows < 1)
         throw new IllegalArgumentException("columns and rows must both be >= 1");
      this.columns = columns;
      this.rows = rows;
   }

   @Override
   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      // Snapshot the current content; we draw scaled copies of it back over the
      // same canvas. Cell boundaries are computed so the grid covers the full
      // width/height exactly with no uncovered strip from integer division.
      ImmutableImage source = image.copy();
      Graphics2D g = (Graphics2D) image.awt().getGraphics();
      try {
         for (int c = 0; c < columns; c++) {
            int x0 = c * w / columns;
            int x1 = (c + 1) * w / columns;
            for (int r = 0; r < rows; r++) {
               int y0 = r * h / rows;
               int y1 = (r + 1) * h / rows;
               g.drawImage(source.awt(), x0, y0, x1 - x0, y1 - y0, null);
            }
         }
      } finally {
         g.dispose();
      }
   }
}
