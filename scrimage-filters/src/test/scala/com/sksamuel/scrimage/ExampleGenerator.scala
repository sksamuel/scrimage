package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import com.sksamuel.scrimage.filter.instagram._
import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import java.awt.Font
import java.io.File
import java.nio.charset.Charset

import com.sksamuel.scrimage.color.Color
import org.apache.commons.io.FileUtils

object ExampleGenerator extends App {

  val image1 = ImmutableImage.fromResource("/bird.jpg")
  val image2 = ImmutableImage.fromResource("/colosseum.jpg")
  val image3 = ImmutableImage.fromResource("/lanzarote.jpg")
  val font = FontUtils.createFont(Font.SANS_SERIF, 48)

  // The table has two image columns (original, filter) and the source image
  // alternates per row across these three samples.
  val images = List(("bird", image1), ("colosseum", image2), ("lanzarote", image3))

  // The large click-through images (input and output) are all scaled to this
  // width so every linked example is a consistent size; height follows the
  // aspect ratio of each source.
  val largeWidth = 1600
  // Thumbnails are rendered at 250px but displayed at 300px (+20%) in the table.
  val thumbWidth = 250
  val displayWidth = 300

  // A different sample of the same size, for filters that take a second image.
  def differentFrom(srcName: String, src: ImmutableImage): ImmutableImage = {
    val alt = if (srcName == "colosseum") image1 else image2
    alt.scaleTo(src.width, src.height)
  }

  // Each filter is built from (sourceName, source) so the ones that need a
  // second image of matching dimensions size correctly against the row's image.
  val filters: List[(String, (String, ImmutableImage) => Filter)] = List(
    ("1977", (_, _) => new Filter1977()),
    ("aden", (_, _) => new AdenFilter()),
    ("alpha_mask", (n, s) => new AlphaMaskFilter(differentFrom(n, s))),
    ("background_blend", (_, _) => new BackgroundBlendFilter()),
    ("black_threshold", (_, _) => new BlackThresholdFilter(38)),
    ("blur", (_, _) => new BlurFilter),
    ("border", (_, _) => new BorderFilter(8, java.awt.Color.GRAY)),
    ("brightness", (_, _) => new BrightnessFilter(1.3f)),
    ("bump", (_, _) => new BumpFilter),
    ("caption", (_, _) => new CaptionFilter("Example", Position.BottomLeft, font, Color.White.toAWT, 1, true, true, Color.White.toAWT, 0.2, new Padding(10))),
    ("caustics", (_, _) => new CausticsFilter(1.2f, 1.0f, 0.3f, 0xff799fff)),
    ("chrome", (_, _) => new ChromeFilter(0.3f, 1.0f)),
    ("color_halftone", (_, _) => new ColorHalftoneFilter()),
    ("colorize", (_, _) => new ColorizeFilter(255, 0, 0, 50)),
    ("contour", (_, _) => new ContourFilter()),
    ("contrast", (_, _) => new ContrastFilter(1.4f)),
    ("crystallize", (_, _) => new CrystallizeFilter()),
    ("despeckle", (_, _) => new DespeckleFilter),
    ("diffuse", (_, _) => new DiffuseFilter(4)),
    ("dissolve", (_, _) => new DissolveFilter(0.4f)),
    ("dither", (_, _) => new DitherFilter),
    ("edge", (_, _) => new EdgeFilter),
    ("emboss", (_, _) => new EmbossFilter),
    ("erode", (_, _) => new ErodeFilter(8)),
    ("error_diffusion_halftone", (_, _) => new ErrorDiffusionHalftoneFilter()),
    ("error_spotter", (n, s) => new ErrorSpotterFilter(differentFrom(n, s))),
    ("gain_bias", (_, _) => new GainBiasFilter(0.7f, 0.6f)),
    ("gamma", (_, _) => new GammaFilter(2)),
    ("gaussian", (_, _) => new GaussianBlurFilter(10)),
    ("glint", (_, _) => new GlintFilter(0.5f, 0.3f, 10, 0.0f)),
    ("glow", (_, _) => new GlowFilter()),
    ("gotham", (_, _) => new GothamFilter()),
    ("grayscale", (_, _) => new GrayscaleFilter),
    ("hsb", (_, _) => new HSBFilter(0.5f, 0, 0)),
    ("invert", (_, _) => new InvertFilter),
    ("invert_alpha", (_, _) => new InvertAlphaFilter),
    ("kaleidoscope", (_, _) => new KaleidoscopeFilter()),
    ("laplace", (_, _) => new LaplaceFilter()),
    ("lensblur", (_, _) => new LensBlurFilter()),
    ("lensflare", (_, _) => new LensFlareFilter),
    ("maximum", (_, _) => new MaximumFilter),
    ("minimum", (_, _) => new MinimumFilter),
    ("mirror", (_, _) => new MirrorFilter),
    ("motionblur", (_, _) => new MotionBlurFilter(Math.PI / 3.0, 20)),
    ("nashville", (_, _) => new NashvilleFilter()),
    ("noise", (_, _) => new NoiseFilter()),
    ("noise_reduction", (_, _) => new NoiseReductionFilter()),
    ("offset", (_, _) => new OffsetFilter(60, 40)),
    ("oil", (_, _) => new OilFilter()),
    ("old_photo", (_, _) => new OldPhotoFilter()),
    ("opacity", (_, _) => new OpacityFilter(0.5f)),
    ("pixelate", (_, _) => new PixelateFilter(6)),
    ("pointillize_square", (_, _) => new PointillizeFilter(PointillizeGridType.Square)),
    ("posterize", (_, _) => new PosterizeFilter()),
    ("prewitt", (_, _) => new PrewittFilter),
    ("quantize", (_, _) => new QuantizeFilter(64)),
    ("rays", (_, _) => new RaysFilter(1.0f, 0.6f, 1.0f)),
    ("rgb", (_, _) => new RGBFilter(0.4f, 0.6f, 0.5f)),
    ("ripple", (_, _) => new RippleFilter(RippleType.Sine, 4f, 4f, 6f, 6f)),
    ("roberts", (_, _) => new RobertsFilter),
    ("rylanders", (_, _) => new RylandersFilter),
    ("salt_and_pepper", (_, _) => new SaltAndPepperFilter(0.05, 0.05)),
    ("sepia", (_, _) => new SepiaFilter),
    ("sharpen", (_, _) => new SharpenFilter),
    ("skeleton", (_, _) => new SkeletonFilter()),
    ("smear_circles", (_, _) => new SmearFilter(SmearType.Circles)),
    ("snow", (_, _) => new SnowFilter()),
    ("sobels", (_, _) => new SobelsFilter),
    ("solarize", (_, _) => new SolarizeFilter),
    ("sparkle", (_, _) => new SparkleFilter(1100, 300, 50, 200, 6)),
    ("split_channels", (_, _) => new SplitChannelsFilter(true, false, false)),
    ("summer", (_, _) => new SummerFilter(true)),
    ("swim", (_, _) => new SwimFilter()),
    ("television", (_, _) => new TelevisionFilter),
    ("threshold", (_, _) => new ThresholdFilter(127)),
    ("tritone", (_, _) => new TritoneFilter(new java.awt.Color(0xFF000044), new java.awt.Color(0xFF0066FF), java.awt.Color.WHITE)),
    ("twirl", (_, s) => new TwirlFilter((Math.PI / 4).toFloat, s.radius.toFloat)),
    ("unsharp", (_, _) => new UnsharpFilter()),
    ("vignette", (_, _) => new VignetteFilter(0.7f, 0.95f, 0.3f, java.awt.Color.BLACK)),
    ("vintage", (_, _) => new VintageFilter),
    ("watermark", (_, _) => new WatermarkFilter("watermark", 50, 200, font, true, 0.5, java.awt.Color.WHITE)),
    ("watermark_cover", (_, _) => new WatermarkCoverFilter("watermark", font, true, 0.2, Color.White.toAWT)),
    ("watermark_stamp", (_, s) => new WatermarkStampFilter("watermark", FontUtils.createFont(Font.SANS_SERIF, s.width / 10), true, 0.2, Color.White.toAWT)),
    ("willow", (_, _) => new WillowFilter())
  ).sortBy(_._1)

  // One unfiltered "original" per sample image. The large click-through image
  // is scaled to largeWidth so it matches the filter outputs; the inline
  // thumbnail is downscaled further.
  for ((name, img) <- images) {
    val large = img.scaleToWidth(largeWidth)
    large.output(new File("examples/filters/" + name + "_original_large.jpeg"))(JpegWriter.compression(95))
    large.scaleToWidth(thumbWidth).forWriter(PngWriter.MaxCompression)
      .write(new File("examples/filters/" + name + "_original_small.png"))
  }

  val baseUrl = "https://raw.github.com/sksamuel/scrimage/master/examples/filters"

  def cell(imgName: String, name: String): String =
    s"<a href='$baseUrl/${imgName}_${name}_large.jpeg'><img width='$displayWidth' src='$baseUrl/${imgName}_${name}_small.png'></a>"

  // A grey/white checkerboard, used as a backdrop so transparency is visible.
  def checkerboard(w: Int, h: Int): ImmutableImage = {
    val img = ImmutableImage.create(w, h)
    val g = img.awt().getGraphics.asInstanceOf[java.awt.Graphics2D]
    val sq = Math.max(8, w / 24)
    var y = 0
    while (y < h) {
      var x = 0
      while (x < w) {
        g.setColor(if (((x / sq) + (y / sq)) % 2 == 0) new java.awt.Color(0xCCCCCC) else java.awt.Color.WHITE)
        g.fillRect(x, y, sq, sq)
        x += sq
      }
      y += sq
    }
    g.dispose()
    img
  }

  val sb = new StringBuffer()
  sb.append("| Filter | Original | Filter |\n")
  sb.append("| ------ | -------- | ------ |\n")

  filters.zipWithIndex.foreach { case ((filterName, factory), i) =>
    val (imgName, img) = images(i % images.size)
    // Filter a copy scaled to largeWidth so the large click-through image is a
    // consistent size across all examples; only the inline thumbnail is
    // downscaled further.
    val source = img.scaleToWidth(largeWidth).copy(java.awt.image.BufferedImage.TYPE_INT_ARGB)
    println("Generating example " + imgName + " " + filterName)
    // invert_alpha only flips the alpha channel, which is invisible on an opaque
    // photo saved as JPEG: fade the source's alpha, invert, and composite over a
    // checkerboard so it shows. NoiseReduction and Blur have no strength parameter
    // (fixed 3x3 kernels), so apply them several times for a clearly visible effect.
    val result =
      if (filterName == "invert_alpha") {
        val w = source.width
        val faded = source.map(p => new java.awt.Color(p.red, p.green, p.blue, if (w > 1) (255 * p.x) / (w - 1) else 255))
        checkerboard(source.width, source.height).overlay(faded.filter(new InvertAlphaFilter()))
      } else if (filterName == "alpha_mask") {
        // AlphaMaskFilter drives the source's alpha from a channel of the mask
        // image. Masking the (opaque) alpha channel of a JPEG mask is a no-op,
        // so sample the mask's red channel (channel 1) for a varying alpha and
        // composite over a checkerboard so the transparency is visible.
        val masked = source.filter(new AlphaMaskFilter(differentFrom(imgName, source), 1))
        checkerboard(source.width, source.height).overlay(masked)
      } else if (filterName == "noise_reduction")
        source.filter(new NoiseReductionFilter(), new NoiseReductionFilter(), new NoiseReductionFilter())
      else if (filterName == "blur")
        source.filter(new BlurFilter(), new BlurFilter(), new BlurFilter(), new BlurFilter(), new BlurFilter())
      else if (filterName == "maximum")
        source.filter(new MaximumFilter(), new MaximumFilter(), new MaximumFilter())
      else if (filterName == "despeckle")
        source.filter(Array.fill(20)(new DespeckleFilter): _*)
      else source.filter(factory(imgName, source))
    result.output(new File("examples/filters/" + imgName + "_" + filterName + "_large.jpeg"))(JpegWriter.compression(95))
    result.scaleToWidth(thumbWidth).forWriter(PngWriter.MaxCompression)
      .write(new File("examples/filters/" + imgName + "_" + filterName + "_small.png"))
    sb.append(s"| $filterName | ${cell(imgName, "original")} | ${cell(imgName, filterName)} |\n")
  }

  FileUtils.write(new File("filters.md"), sb.toString, Charset.defaultCharset())

  def genBlurAndStretchExample(): Unit = {
    val filterName = "blur_and_stretch"
    val path = "examples/images/"
    val baseFilename = "lanzarote"
    val resized = {
      val tmp = image3.scaleToHeight(400)
      tmp.resizeTo(tmp.width / 2, tmp.height)
    }
    resized.output(new File(path + baseFilename + "_small.jpeg"))(JpegWriter.compression(95))
    val radius = 12
    val bloom = 2
    val bloomThreshold = 255
    val sides = 10
    val filter = new LensBlurFilter(radius, bloom, bloomThreshold, sides)
    val fileName = s"${baseFilename}_$filterName.jpeg"
    println("Generating example " + fileName)
    val fgImage = resized
    val targetWidth = fgImage.width * 2
    val targetHeight = fgImage.height
    resized
      .cover(targetWidth, targetHeight, ScaleMethod.FastScale)
      .filter(filter)
      .overlay(fgImage, x = (targetWidth - fgImage.width) / 2, y = (targetHeight - fgImage.height) / 2)
      .output(new File(path + fileName))(JpegWriter.compression(95))
  }

  genBlurAndStretchExample()
}
