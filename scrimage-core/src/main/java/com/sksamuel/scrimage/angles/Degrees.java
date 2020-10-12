package com.sksamuel.scrimage.angles;

public class Degrees {
   public final int value;

   public Degrees(int value) {
      this.value = value;
   }

   public Radians toRadians() {
      return new Radians(((double) value * Math.PI / 180.0));
   }
}
