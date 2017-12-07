package com.sksamuel.scrimage;

public class Area {

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    public Area(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        if (x != area.x) return false;
        if (y != area.y) return false;
        if (w != area.w) return false;
        return h == area.h;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + w;
        result = 31 * result + h;
        return result;
    }
}
