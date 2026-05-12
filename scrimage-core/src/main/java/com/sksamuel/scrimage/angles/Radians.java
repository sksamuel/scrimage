package com.sksamuel.scrimage.angles;

public class Radians {
   public final double value;

   public Radians(double value) {
      this.value = value;
   }

   public Degrees toDegrees() {
      // Math.round (not int cast) so values close to but slightly less
      // than an integer degree round to the nearest. (int) cast truncates
      // toward zero, so e.g. 89.99999° in radians → 89° instead of 90°.
      return new Degrees((int) Math.round(Math.toDegrees(value)));
   }
}
