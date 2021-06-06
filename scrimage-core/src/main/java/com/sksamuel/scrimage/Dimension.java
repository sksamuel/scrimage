package com.sksamuel.scrimage;

public class Dimension {

    private final int x;
    private final int y;

    public Dimension(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimension dimension = (Dimension) o;

        if (x != dimension.x) return false;
        return y == dimension.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

   @Override
   public String toString() {
      return "Dimension{" +
         "x=" + x +
         ", y=" + y +
         '}';
   }
}
