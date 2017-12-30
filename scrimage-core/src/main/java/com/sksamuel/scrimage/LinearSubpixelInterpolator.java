package com.sksamuel.scrimage;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class LinearSubpixelInterpolator implements SubpixelInterpolator {

    private final AwtImage awt;
    private final int width;
    private final int height;

    public LinearSubpixelInterpolator(AwtImage awt) {
        this.awt = awt;
        width = awt.width;
        height = awt.height;
    }

    // As a part of linear interpolation, determines the integer coordinates
    // of the pixel's neighbors, as well as the amount of weight each should
    // get in the weighted average.
    // Operates on one dimension at a time.
    private List<Pair<Integer, Double>> integerPixelCoordinatesAndWeights(double d, int numPixels) {
        if (d <= 0.5) return Collections.singletonList(new Pair<>(0, 1.0));
        else if (d >= numPixels - 0.5) return Collections.singletonList(new Pair<>(numPixels - 1, 1.0));
        else {
            double shifted = d - 0.5;
            double floor = Math.floor(shifted);
            double floorWeight = 1 - (shifted - floor);
            double ceil = Math.ceil(shifted);
            double ceilWeight = 1 - floorWeight;
            assert (floorWeight + ceilWeight == 1);
            return Arrays.asList(new Pair<>((int) floor, floorWeight), new Pair<>((int) ceil, ceilWeight));
        }
    }

    public double[][] summands(double x, double y) {

        List<Pair<Integer, Double>> xIntsAndWeights = integerPixelCoordinatesAndWeights(x, width);
        List<Pair<Integer, Double>> yIntsAndWeights = integerPixelCoordinatesAndWeights(y, height);

        double[][] summands = new double[xIntsAndWeights.size() * yIntsAndWeights.size()][];
        int k = 0;
        for (Pair<Integer, Double> xintweight : xIntsAndWeights) {
            for (Pair<Integer, Double> yintweight : yIntsAndWeights) {
                double weight = xintweight.getValue() * yintweight.getValue();

                if (weight == 0) {
                    summands[k++] = new double[]{0.0, 0.0, 0.0, 0.0};
                } else {
                    Pixel px = awt.pixel(xintweight.getKey(), yintweight.getKey());
                    summands[k++] = new double[]{
                            weight * px.alpha(),
                            weight * px.red(),
                            weight * px.green(),
                            weight * px.blue()
                    };
                }
            }
        }

        return summands;
    }

    @Override
    public int subpixel(double x, double y) {
        assert (x >= 0 && x < width && y >= 0 && y < height);

        // These are the summands in the weighted averages.
        // Note there are 4 weighted averages: one for each channel (a, r, g, b).
        double[][] summands = summands(x, y);

        // We perform the weighted averaging (a summation).
        // First though, we need to transpose so that we sum within channels,
        // not within pixels.
        List<Double> argb = Arrays.stream(summands).map(ds -> Arrays.stream(ds).sum()).collect(Collectors.toList());

        return PixelTools.argb(
                (int) Math.round(argb.get(0)),
                (int) Math.round(argb.get(1)),
                (int) Math.round(argb.get(2)),
                (int) Math.round(argb.get(3))
        );
    }
}
