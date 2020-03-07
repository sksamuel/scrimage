package com.sksamuel.scrimage.metadata;

import java.util.Arrays;
import java.util.Optional;

public enum Orientation {

   // 0 degrees – the correct orientation, no adjustment is required.
   Zero(1),
   // 0 degrees but flipped horizontally.
   ZeroMirrored(2),
   // 180 rotated.
   OneEighty(3),
   // 180 degrees rotated and flipped horizontally.
   OneEightyMirrored(4),
   // 90 degrees rotated.
   Ninety(5),
   // 90 degrees rotated and flipped horizontally.
   NinetyMirrored(6),
   // 270 degrees rotated.
   TwoSeventy(7),
   // 270 degrees rotated and flipped horizontally.
   TwoSeventyMirrored(8);

   private final int value;

   Orientation(int value) {
      this.value = value;
   }

   /**
    * Returns the [Orientation] enum that matches the given raw value.
    */
   public static Optional<Orientation> fromRawValue(int i) {
      return Arrays.stream(Orientation.values()).filter(o -> o.value == i).findFirst();
   }
}
