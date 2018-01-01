package com.sksamuel.scrimage.filter;

public class Padding {

    public final int top;
    public final int right;
    public final int bottom;
    public final int left;

    public Padding(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Padding(int constant) {
        this(constant, constant, constant, constant);
    }
}
