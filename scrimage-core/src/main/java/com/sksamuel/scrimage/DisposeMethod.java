package com.sksamuel.scrimage;

import java.util.Arrays;

/**
 * When gifs transition from frame to frame, the new frame is overlaid on top of the previous frame, with the
 * 'disposeMethod' metadata property of each frame telling the current frame what to do with the previous frame
 * once the new frame is displayed.
 */
public enum DisposeMethod {

   /**
    * 'none' or unspecified replaces the entire non-transparent frame with the new one.
    */
   NONE(0, "none"),

   /**
    * 'doNotDispose' tells the current frame to display the pixels from the previous frame that
    * were not replaced with the new frame. When not dealing with transparent frames 'none' and
    * 'doNotDispose' behave the same.
    */
   DO_NOT_DISPOSE(1, "doNotDispose"),

   /**
    * 'restoreToBackgroundColor' makes the background color or background tile show through the transparent
    * pixels of the new frame (replacing the image areas of the previous frame).
    */
   RESTORE_TO_BACKGROUND_COLOR(2, "restoreToBackgroundColor"),

   /**
    * 'restoreToPrevious' restores to the state of the most recent un-disposed frame. For example, if you
    * have a static background that is set to Do Not Dispose, that image will reappear in the areas left
    * by a replaced frame (until a new frame has been declared as 'doNotDispose').
    */
   RESTORE_TO_PREVIOUS(3, "restoreToPrevious");

   private final int id;
   private final String disposeMethodValue;

   DisposeMethod(int id, String disposeMethodValue) {
      this.id = id;
      this.disposeMethodValue = disposeMethodValue;
   }

   public String getDisposeMethodValue() {
      return disposeMethodValue;
   }

   public static DisposeMethod getDisposeMethodFromId(int id) {
      return Arrays.stream(DisposeMethod.values())
         .filter(disposeMethod -> disposeMethod.id == id)
         .findFirst().orElseThrow(() -> new RuntimeException("DisposeMethod with id [" + id + "] is invalid"));
   }
}
