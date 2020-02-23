package com.sksamuel.scrimage;

public class Radians {
    public final double value;

    public Radians(double value) {
        this.value = value;
    }

    public Degrees toDegrees() {
        return new Degrees((int) (value * 180.0 / Math.PI));
    }
}
