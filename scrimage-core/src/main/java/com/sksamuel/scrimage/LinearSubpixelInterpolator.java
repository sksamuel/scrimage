package com.sksamuel.scrimage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Pair<A, B> {
    private final A a;
    private final B b;

    Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}

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
                double weight = xintweight.getB() * yintweight.getB();

                if (weight == 0) {
                    summands[k++] = new double[]{0.0, 0.0, 0.0, 0.0};
                } else {
                    Pixel px = awt.pixel(xintweight.getA(), yintweight.getA());
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

    private int sumChannel(int channel, double[][] summands) {
        return (int) Math.round(Arrays.stream(summands).mapToDouble(ds -> ds[channel]).sum());
    }

    @Override
    public int subpixel(double x, double y) {
        assert (x >= 0 && x < width && y >= 0 && y < height);

        // These are the summands in the weighted averages.
        // Note there are 4 weighted averages: one for each channel (a, r, g, b).
        double[][] summands = summands(x, y);

        // We perform the weighted averaging (a summation).
        // We need to sum within channels not within pixels
        int a = sumChannel(0, summands);
        int r = sumChannel(1, summands);
        int g = sumChannel(2, summands);
        int b = sumChannel(3, summands);

        return PixelTools.argb(a, r, g, b);
    }
}
