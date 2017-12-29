package com.sksamuel.scrimage;

import javafx.util.Pair;

import java.awt.image.BufferedImage;

interface SubpixelInterpolator {
    int subpixel(double x, double y);
}


class LinearSubpixelInterpolator implements SubpixelInterpolator {

    private final BufferedImage awt;
    private final int width;
    private final int height;

    public LinearSubpixelInterpolator(BufferedImage awt) {
        this.awt = awt;
        width = awt.getWidth();
        height = awt.getHeight();
    }

    // As a part of linear interpolation, determines the integer coordinates
    // of the pixel's neighbors, as well as the amount of weight each should
    // get in the weighted average.
    // Operates on one dimension at a time.
    private Pair<Integer, Double>[] integerPixelCoordinatesAndWeights(double d, int numPixels) {
        if (d <= 0.5) return new Pair[]{((0, 1.0)) }
      else if (d >= numPixels - 0.5) List((numPixels - 1, 1.0))
      else{
            double shifted = d - 0.5;
            double floor = Math.floor(shifted);
            double floorWeight = 1 - (shifted - floor);
            double ceil = Math.ceil(shifted);
            val ceilWeight = 1 - floorWeight
            assert (floorWeight + ceilWeight == 1)
            List((floor.toInt, floorWeight), (ceil.toInt, ceilWeight))
        }
    }

    @Override
    public int subpixel(double x, double y) {
        assert (x >= 0 && x < width && y >= 0 && y < height);

        Pair<Integer, Double>[] xIntsAndWeights = integerPixelCoordinatesAndWeights(x, width);
        Pair<Integer, Double>[] yIntsAndWeights = integerPixelCoordinatesAndWeights(y, height);

        // These are the summands in the weighted averages.
        // Note there are 4 weighted averages: one for each channel (a, r, g, b).
        val summands = for (
                (xInt, xWeight) <-xIntsAndWeights;
        (yInt, yWeight) <-yIntsAndWeights
    )yield {
            val weight = xWeight * yWeight
            if (weight == 0) List(0.0, 0.0, 0.0, 0.0)
            else {
                val px = pixel(xInt, yInt)
                List(
                        weight * px.alpha,
                        weight * px.red,
                        weight * px.green,
                        weight * px.blue)
            }
        }

        // We perform the weighted averaging (a summation).
        // First though, we need to transpose so that we sum within channels,
        // not within pixels.
        val List (a, r, g, b) =summands.transpose.map(_.sum)

        PixelTools.argb(a.round.toInt, r.round.toInt, g.round.toInt, b.round.toInt)
    }
}