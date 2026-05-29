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
import java.awt.image.BufferedImage;

/**
 * Repeats (tiles) the source image across a larger canvas. Given {@code columns} and
 * {@code rows}, the output image is laid out as a grid of {@code columns} copies across
 * and {@code rows} copies down, with each copy drawn at the source image's full size.
 * <p>
 * Unlike a size-preserving {@link Filter}, this grows the canvas: the result has
 * dimensions {@code (width * columns) x (height * rows)}. For example,
 * {@code new RepeaterFilter(2, 3).apply(image)} returns an image twice as wide and three
 * times as tall as the original, with the original tiled across its rows and columns.
 * <p>
 * Because the output dimensions differ from the input, this is not implemented as a
 * {@link Filter} (which draws into a canvas of the original size); call
 * {@link #apply(ImmutableImage)} directly to obtain the enlarged, tiled image.
 */
public class RepeaterFilter {

   private final int columns;
   private final int rows;

   public RepeaterFilter(int columns, int rows) {
      if (columns < 1 || rows < 1)
         throw new IllegalArgumentException("columns and rows must both be >= 1");
      this.columns = columns;
      this.rows = rows;
   }

   /**
    * Returns a new image that tiles the given image in a {@code columns x rows} grid,
    * each tile drawn at the source image's full size. The returned image has dimensions
    * {@code (image.width * columns) x (image.height * rows)}.
    *
    * @param image the image to tile
    * @return a new, enlarged image containing the tiled copies
    */
   public ImmutableImage apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;

      // Preserve the source buffer type where possible; TYPE_CUSTOM cannot be used to
      // construct a BufferedImage, so fall back to the default (ARGB) in that case.
      int type = image.awt().getType();
      if (type == BufferedImage.TYPE_CUSTOM) type = ImmutableImage.DEFAULT_DATA_TYPE;

      ImmutableImage out = ImmutableImage.create(w * columns, h * rows, type);
      Graphics2D g = (Graphics2D) out.awt().getGraphics();
      try {
         for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
               g.drawImage(image.awt(), c * w, r * h, w, h, null);
            }
         }
      } finally {
         g.dispose();
      }
      return out;
   }
}
