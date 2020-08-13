package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec

class Issue204 : FunSpec() {
   init {
      test("java.lang.IllegalStateException: Source and destination must store pixels as INT. #204") {
         ImmutableImage.loader().fromResource("/issue204.png").filter(DissolveFilter(0.6F))
         ImmutableImage.loader().fromResource("/issue204.png").filter(SummerFilter(true))
         ImmutableImage.loader().fromResource("/issue204.png").filter(SnowFilter())
         ImmutableImage.loader().fromResource("/issue204.png").filter(NashvilleFilter())
         ImmutableImage.loader().fromResource("/issue204.png").filter(OldPhotoFilter())
         ImmutableImage.loader().fromResource("/issue204.png").filter(VintageFilter())
      }
   }
}
