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
import com.sksamuel.scrimage.transform.Transform;

import java.awt.Graphics2D;

/**
 * Repeats (tiles) the source image across a grid, producing a larger image.
 * Given {@code columns} and {@code rows}, the output is {@code columns} times
 * the source width and {@code rows} times the source height, with the source
 * drawn at full size into every cell.
 * <p>
 * For example {@code new TileFilter(2, 3)} produces an image twice as wide
 * and three times as tall as the source, tiled 2 across and 3 down.
 * <p>
 * Because the result changes the image dimensions it is a {@link Transform}
 * (applied via {@code image.transform(...)}) rather than a size-preserving
 * {@link Filter}.
 */
public class TileFilter implements Transform {

   private final int columns;
   private final int rows;

   public TileFilter(int columns, int rows) {
      if (columns < 1 || rows < 1)
         throw new IllegalArgumentException("columns and rows must both be >= 1");
      this.columns = columns;
      this.rows = rows;
   }

   @Override
   public ImmutableImage apply(ImmutableImage input) {
      int w = input.width;
      int h = input.height;
      ImmutableImage output = ImmutableImage.create(w * columns, h * rows);
      Graphics2D g = (Graphics2D) output.awt().getGraphics();
      try {
         for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
               g.drawImage(input.awt(), c * w, r * h, null);
            }
         }
      } finally {
         g.dispose();
      }
      return output;
   }
}
