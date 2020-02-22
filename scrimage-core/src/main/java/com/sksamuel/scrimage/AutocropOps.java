package com.sksamuel.scrimage;

import com.sksamuel.scrimage.color.Color;

public class AutocropOps {

    /**
     * Starting with the given column index, will return the first column index
     * which contains a colour that does not match the given color.
     */
    public static int scanright(Color color, int height, int width, int col, PixelsExtractor f, int tolerance) {
        if (col == width || !PixelTools.colorMatches(color, tolerance, f.apply(new Area(col, 0, 1, height))))
            return col;
        else
            return scanright(color, height, width, col + 1, f, tolerance);
    }

    public static int scanleft(Color color, int height, int width, int col, PixelsExtractor f, int tolerance) {
        if (col == 0 || !PixelTools.colorMatches(color, tolerance, f.apply(new Area(col, 0, 1, height))))
            return col;
        else
            return scanleft(color, height, width, col - 1, f, tolerance);
    }

    public static int scandown(Color color, int height, int width, int row, PixelsExtractor f, int tolerance) {
        if (row == height || !PixelTools.colorMatches(color, tolerance, f.apply(new Area(0, row, width, 1))))
            return row;
        else
            return scandown(color, height, width, row + 1, f, tolerance);
    }

    public static int scanup(Color color, int height, int width, int row, PixelsExtractor f, int tolerance) {
        if (row == 0 || !PixelTools.colorMatches(color, tolerance, f.apply(new Area(0, row, width, 1))))
            return row;
        else
            return scanup(color, height, width, row - 1, f, tolerance);
    }
}