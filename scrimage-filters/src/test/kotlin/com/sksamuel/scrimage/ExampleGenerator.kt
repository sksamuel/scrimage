package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter.*
import com.sksamuel.scrimage.filter.instagram.*
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

/**
 * Generates the example images and the filters docs table. Run the [main]
 * method from the project root; outputs are written to examples/filters and
 * filters.md.
 *
 * Ported from the original Scala ExampleGenerator.
 */
object ExampleGenerator {

   private val image1 = ImmutableImage.fromResource("/bird.jpg")
   private val image2 = ImmutableImage.fromResource("/colosseum.jpg")
   private val image3 = ImmutableImage.fromResource("/lanzarote.jpg")
   private val font = FontUtils.createFont(Font.SANS_SERIF, 48)

   // The table has two image columns (original, filter) and the source image
   // alternates per row across these three samples.
   private val images = listOf("bird" to image1, "colosseum" to image2, "lanzarote" to image3)

   // The large click-through images (input and output) are all scaled to this
   // width so every linked example is a consistent size; height follows the
   // aspect ratio of each source.
   private const val largeWidth = 1600

   // Thumbnails are rendered at 250px but displayed at 300px (+20%) in the table.
   private const val thumbWidth = 250
   private const val displayWidth = 300

   private val baseUrl = "https://raw.github.com/sksamuel/scrimage/master/examples/filters"

   // A different sample of the same size, for filters that take a second image.
   private fun differentFrom(srcName: String, src: ImmutableImage): ImmutableImage {
      val alt = if (srcName == "colosseum") image1 else image2
      return alt.scaleTo(src.width, src.height)
   }

   // A grey/white checkerboard, used as a backdrop so transparency is visible.
   private fun checkerboard(w: Int, h: Int): ImmutableImage {
      val img = ImmutableImage.create(w, h)
      val g = img.awt().graphics as Graphics2D
      val sq = maxOf(8, w / 24)
      var y = 0
      while (y < h) {
         var x = 0
         while (x < w) {
            g.color = if (((x / sq) + (y / sq)) % 2 == 0) Color(0xCCCCCC) else Color.WHITE
            g.fillRect(x, y, sq, sq)
            x += sq
         }
         y += sq
      }
      g.dispose()
      return img
   }

   private fun cell(imgName: String, name: String): String =
      "<a href='$baseUrl/${imgName}_${name}_large.jpeg'><img width='$displayWidth' src='$baseUrl/${imgName}_${name}_small.png'></a>"

   // Each filter is built from (sourceName, source) so the ones that need a
   // second image of matching dimensions size correctly against the row's image.
   private val filters: List<Pair<String, (String, ImmutableImage) -> Filter>> = listOf<Pair<String, (String, ImmutableImage) -> Filter>>(
      "1977" to { _, _ -> Filter1977() },
      "aden" to { _, _ -> AdenFilter() },
      "alpha_mask" to { n, s -> AlphaMaskFilter(differentFrom(n, s)) },
      "amaro" to { _, _ -> AmaroFilter() },
      "ashby" to { _, _ -> AshbyFilter() },
      "background_blend" to { _, _ -> BackgroundBlendFilter() },
      "black_threshold" to { _, _ -> BlackThresholdFilter(38.0) },
      "blur" to { _, _ -> BlurFilter() },
      "border" to { _, _ -> BorderFilter(8, Color.GRAY) },
      "brannan" to { _, _ -> BrannanFilter() },
      "brightness" to { _, _ -> BrightnessFilter(1.3f) },
      "brooklyn" to { _, _ -> BrooklynFilter() },
      "bump" to { _, _ -> BumpFilter() },
      "caption" to { _, _ -> CaptionFilter("Example", Position.BottomLeft, font, Color.WHITE, 1.0, true, true, Color.WHITE, 0.2, Padding(10)) },
      "caustics" to { _, _ -> CausticsFilter(1.2f, 1.0f, 0.3f, 0xff799fff.toInt()) },
      "charmes" to { _, _ -> CharmesFilter() },
      "chrome" to { _, _ -> ChromeFilter(0.3f, 1.0f) },
      "clarendon" to { _, _ -> ClarendonFilter() },
      "color_halftone" to { _, _ -> ColorHalftoneFilter() },
      "colorize" to { _, _ -> ColorizeFilter(255, 0, 0, 50) },
      "contour" to { _, _ -> ContourFilter() },
      "contrast" to { _, _ -> ContrastFilter(1.4) },
      "crema" to { _, _ -> CremaFilter() },
      "crystallize" to { _, _ -> CrystallizeFilter() },
      "despeckle" to { _, _ -> DespeckleFilter() },
      "diffuse" to { _, _ -> DiffuseFilter(4f) },
      "dissolve" to { _, _ -> DissolveFilter(0.4f) },
      "dither" to { _, _ -> DitherFilter() },
      "dogpatch" to { _, _ -> DogpatchFilter() },
      "dominant_gradient" to { _, _ -> DominantGradientFilter() },
      "earlybird" to { _, _ -> EarlybirdFilter() },
      "edge" to { _, _ -> EdgeFilter() },
      "emboss" to { _, _ -> EmbossFilter() },
      "erode" to { _, _ -> ErodeFilter(8) },
      "error_diffusion_halftone" to { _, _ -> ErrorDiffusionHalftoneFilter() },
      "error_spotter" to { n, s -> ErrorSpotterFilter(differentFrom(n, s)) },
      "gain_bias" to { _, _ -> GainBiasFilter(0.7f, 0.6f) },
      "gamma" to { _, _ -> GammaFilter(2.0) },
      "gaussian" to { _, _ -> GaussianBlurFilter(10) },
      "gingham" to { _, _ -> GinghamFilter() },
      "glint" to { _, _ -> GlintFilter(0.5f, 0.3f, 10, 0.0f) },
      "glow" to { _, _ -> GlowFilter() },
      "gotham" to { _, _ -> GothamFilter() },
      "grayscale" to { _, _ -> GrayscaleFilter() },
      "hefe" to { _, _ -> HefeFilter() },
      "hsb" to { _, _ -> HSBFilter(0.5f, 0f, 0f) },
      "hudson" to { _, _ -> HudsonFilter() },
      "invert" to { _, _ -> InvertFilter() },
      "invert_alpha" to { _, _ -> InvertAlphaFilter() },
      "juno" to { _, _ -> JunoFilter() },
      "kaleidoscope" to { _, _ -> KaleidoscopeFilter() },
      "laplace" to { _, _ -> LaplaceFilter() },
      "lensblur" to { _, _ -> LensBlurFilter() },
      "lensflare" to { _, _ -> LensFlareFilter() },
      "ludwig" to { _, _ -> LudwigFilter() },
      "maximum" to { _, _ -> MaximumFilter() },
      "minimum" to { _, _ -> MinimumFilter() },
      "mirror" to { _, _ -> MirrorFilter() },
      "moon" to { _, _ -> MoonFilter() },
      "motionblur" to { _, _ -> MotionBlurFilter(Math.PI / 3.0, 20.0) },
      "nashville" to { _, _ -> NashvilleFilter() },
      "noise" to { _, _ -> NoiseFilter() },
      "noise_reduction" to { _, _ -> NoiseReductionFilter() },
      "offset" to { _, _ -> OffsetFilter(60, 40) },
      "oil" to { _, _ -> OilFilter() },
      "old_photo" to { _, _ -> OldPhotoFilter() },
      "opacity" to { _, _ -> OpacityFilter(0.5f) },
      "perpetua" to { _, _ -> PerpetuaFilter() },
      "pixelate" to { _, _ -> PixelateFilter(6) },
      "pointillize_square" to { _, _ -> PointillizeFilter(PointillizeGridType.Square) },
      "poprocket" to { _, _ -> PoprocketFilter() },
      "posterize" to { _, _ -> PosterizeFilter() },
      "prewitt" to { _, _ -> PrewittFilter() },
      "quantize" to { _, _ -> QuantizeFilter(64) },
      "rays" to { _, _ -> RaysFilter(1.0f, 0.6f, 1.0f) },
      "rgb" to { _, _ -> RGBFilter(0.4f, 0.6f, 0.5f) },
      "ripple" to { _, _ -> RippleFilter(RippleType.Sine, 4f, 4f, 6f, 6f) },
      "roberts" to { _, _ -> RobertsFilter() },
      "rylanders" to { _, _ -> RylandersFilter() },
      "salt_and_pepper" to { _, _ -> SaltAndPepperFilter(0.05, 0.05) },
      "sepia" to { _, _ -> SepiaFilter() },
      "sharpen" to { _, _ -> SharpenFilter() },
      "skeleton" to { _, _ -> SkeletonFilter() },
      "slumber" to { _, _ -> SlumberFilter() },
      "smear_circles" to { _, _ -> SmearFilter(SmearType.Circles) },
      "snow" to { _, _ -> SnowFilter() },
      "sobels" to { _, _ -> SobelsFilter() },
      "solarize" to { _, _ -> SolarizeFilter() },
      "sparkle" to { _, _ -> SparkleFilter(1100, 300, 50, 200, 6) },
      "split_channels" to { _, _ -> SplitChannelsFilter(true, false, false) },
      "summer" to { _, _ -> SummerFilter(true) },
      "sutro" to { _, _ -> SutroFilter() },
      "swim" to { _, _ -> SwimFilter() },
      "television" to { _, _ -> TelevisionFilter() },
      "threshold" to { _, _ -> ThresholdFilter(127) },
      "toaster" to { _, _ -> ToasterFilter() },
      "tritone" to { _, _ -> TritoneFilter(Color(0xFF000044.toInt()), Color(0xFF0066FF.toInt()), Color.WHITE) },
      "twirl" to { _, s -> TwirlFilter((Math.PI / 4).toFloat(), s.radius().toFloat()) },
      "unsharp" to { _, _ -> UnsharpFilter() },
      "valencia" to { _, _ -> ValenciaFilter() },
      "vignette" to { _, _ -> VignetteFilter(0.7f, 0.95f, 0.3f, Color.BLACK) },
      "vintage" to { _, _ -> VintageFilter() },
      "walden" to { _, _ -> WaldenFilter() },
      "watermark" to { _, _ -> WatermarkFilter("watermark", 50, 200, font, true, 0.5, Color.WHITE) },
      "watermark_cover" to { _, _ -> WatermarkCoverFilter("watermark", font, true, 0.2, Color.WHITE) },
      "watermark_stamp" to { _, s -> WatermarkStampFilter("watermark", FontUtils.createFont(Font.SANS_SERIF, s.width / 10), true, 0.2, Color.WHITE) },
      "willow" to { _, _ -> WillowFilter() }
   ).sortedBy { it.first }

   @JvmStatic
   fun main(args: Array<String>) {
      // One unfiltered "original" per sample image. The large click-through image
      // is scaled to largeWidth so it matches the filter outputs; the inline
      // thumbnail is downscaled further.
      for ((name, img) in images) {
         val large = img.scaleToWidth(largeWidth)
         large.forWriter(JpegWriter.compression(95)).write(File("examples/filters/${name}_original_large.jpeg"))
         large.scaleToWidth(thumbWidth).forWriter(PngWriter.MaxCompression).write(File("examples/filters/${name}_original_small.png"))
      }

      val sb = StringBuilder()
      sb.append("| Filter | Original | Filter |\n")
      sb.append("| ------ | -------- | ------ |\n")

      filters.forEachIndexed { i, (filterName, factory) ->
         val (imgName, img) = images[i % images.size]
         // Filter a copy scaled to largeWidth so the large click-through image is a
         // consistent size across all examples; only the inline thumbnail is
         // downscaled further.
         val source = img.scaleToWidth(largeWidth).copy(BufferedImage.TYPE_INT_ARGB)
         println("Generating example $imgName $filterName")
         // invert_alpha only flips the alpha channel, which is invisible on an opaque
         // photo saved as JPEG: fade the source's alpha, invert, and composite over a
         // checkerboard so it shows. NoiseReduction and Blur have no strength parameter
         // (fixed 3x3 kernels), so apply them several times for a clearly visible effect.
         val result = when (filterName) {
            "invert_alpha" -> {
               val w = source.width
               val faded = source.map { p -> Color(p.red(), p.green(), p.blue(), if (w > 1) (255 * p.x) / (w - 1) else 255) }
               checkerboard(source.width, source.height).overlay(faded.filter(InvertAlphaFilter()))
            }
            "alpha_mask" -> {
               // AlphaMaskFilter drives the source's alpha from a channel of the mask
               // image. Masking the (opaque) alpha channel of a JPEG mask is a no-op,
               // so sample the mask's red channel (channel 1) for a varying alpha and
               // composite over a checkerboard so the transparency is visible.
               val masked = source.filter(AlphaMaskFilter(differentFrom(imgName, source), 1))
               checkerboard(source.width, source.height).overlay(masked)
            }
            "noise_reduction" -> source.filter(NoiseReductionFilter(), NoiseReductionFilter(), NoiseReductionFilter())
            "blur" -> source.filter(BlurFilter(), BlurFilter(), BlurFilter(), BlurFilter(), BlurFilter())
            "maximum" -> source.filter(MaximumFilter(), MaximumFilter(), MaximumFilter())
            "despeckle" -> source.filter(*Array(20) { DespeckleFilter() })
            else -> source.filter(factory(imgName, source))
         }
         result.forWriter(JpegWriter.compression(95)).write(File("examples/filters/${imgName}_${filterName}_large.jpeg"))
         result.scaleToWidth(thumbWidth).forWriter(PngWriter.MaxCompression).write(File("examples/filters/${imgName}_${filterName}_small.png"))
         sb.append("| $filterName | ${cell(imgName, "original")} | ${cell(imgName, filterName)} |\n")
      }

      File("filters.md").writeText(sb.toString())
   }
}
