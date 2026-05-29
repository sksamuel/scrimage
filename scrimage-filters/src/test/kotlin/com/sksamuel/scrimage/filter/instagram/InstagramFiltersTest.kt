package com.sksamuel.scrimage.filter.instagram

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InstagramFiltersTest : FunSpec({

   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(120)

   val all = listOf(
      Filter1977(), AdenFilter(), AmaroFilter(), AshbyFilter(), BrannanFilter(), BrooklynFilter(),
      CharmesFilter(), ClarendonFilter(), CremaFilter(), DogpatchFilter(), EarlybirdFilter(),
      GinghamFilter(), GinzaFilter(), HefeFilter(), HelenaFilter(), HudsonFilter(), InkwellFilter(),
      JunoFilter(), KelvinFilter(), LarkFilter(), LofiFilter(), LudwigFilter(), MavenFilter(),
      MayfairFilter(), MoonFilter(), PerpetuaFilter(), PoprocketFilter(), ReyesFilter(), RiseFilter(),
      SierraFilter(), SkylineFilter(), SlumberFilter(), StinsonFilter(), SutroFilter(), ToasterFilter(),
      ValenciaFilter(), VesperFilter(), WaldenFilter(), WillowFilter(), Xpro2Filter()
   )

   all.forEach { f ->
      test("${f.javaClass.simpleName} runs and preserves dimensions") {
         val out = image.filter(f)
         out.width shouldBe image.width
         out.height shouldBe image.height
      }
   }

   // A uniform image isolates the overlay's spatial behaviour from image content.
   val gray = ImmutableImage.create(100, 100).map { java.awt.Color(128, 128, 128) }

   test("a radial-overlay filter (mayfair) varies from centre to corner") {
      val out = gray.filter(MayfairFilter())
      out.pixel(50, 50).argb shouldNotBe out.pixel(0, 0).argb
   }

   test("a linear-overlay filter (perpetua) varies from top to bottom") {
      val out = gray.filter(PerpetuaFilter())
      out.pixel(50, 0).argb shouldNotBe out.pixel(50, 99).argb
   }

   test("a no-overlay filter (lofi) stays uniform on a uniform image") {
      val out = gray.filter(LofiFilter())
      out.pixel(0, 0).argb shouldBe out.pixel(99, 99).argb
   }
})
