package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.DisposeMethod
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.assertThrows

class DisposeMethodTest : FunSpec({

   test("Test getDisposeMethodFromId") {
      assertThrows<RuntimeException>("DisposeMethod with id [-1] is invalid") {
         DisposeMethod.getDisposeMethodFromId(-1)
      }
      assert(DisposeMethod.getDisposeMethodFromId(0) == DisposeMethod.NONE)
      assert(DisposeMethod.getDisposeMethodFromId(1) == DisposeMethod.DO_NOT_DISPOSE)
      assert(DisposeMethod.getDisposeMethodFromId(2) == DisposeMethod.RESTORE_TO_BACKGROUND_COLOR)
      assert(DisposeMethod.getDisposeMethodFromId(3) == DisposeMethod.RESTORE_TO_PREVIOUS)
      assertThrows<RuntimeException>("DisposeMethod with id [4] is invalid") {
         DisposeMethod.getDisposeMethodFromId(4)
      }
   }

   test("Test strings match spec") {
      assert(DisposeMethod.NONE.disposeMethodValue == "none")
      assert(DisposeMethod.DO_NOT_DISPOSE.disposeMethodValue == "doNotDispose")
      assert(DisposeMethod.RESTORE_TO_BACKGROUND_COLOR.disposeMethodValue == "restoreToBackgroundColor")
      assert(DisposeMethod.RESTORE_TO_PREVIOUS.disposeMethodValue == "restoreToPrevious")
   }

})
