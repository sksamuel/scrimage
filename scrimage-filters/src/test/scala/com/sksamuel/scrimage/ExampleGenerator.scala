package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import java.awt.Font
import java.io.File
import java.nio.charset.Charset
import org.apache.commons.io.FileUtils

object ExampleGenerator extends App {

  val image1 = Image.fromResource("/bird.jpg")
  val image2 = Image.fromResource("/colosseum.jpg")
  val image3 = Image.fromResource("/lanzarote.jpg")
  val font = FontUtils.createFont(Font.SANS_SERIF, 48)

  val filters: List[(String, Filter)] = List(
    ("black_threshold", new BlackThresholdFilter(35)),
    ("blur", new BlurFilter),
    ("border", new BorderFilter(8)),
    ("brightness", new BrightnessFilter(1.3f)),
    ("bump", new BumpFilter),
    ("caption", new CaptionFilter("Example", Position.BottomLeft, font, Color.White.toAWT, 1, true, true, Color.White.toAWT, 0.2, new Padding(10))),
    ("chrome", new ChromeFilter()),
    ("color_halftone", new ColorHalftoneFilter()),
    ("colorize", new ColorizeFilter(255, 0, 0, 50)),
    ("contour", new ContourFilter()),
    ("contrast", new ContrastFilter(1.3f)),
    ("crystallize", new CrystallizeFilter()),
    ("despeckle", new DespeckleFilter),
    ("diffuse", new DiffuseFilter(4)),
    ("dither", new DitherFilter),
    ("edge", new EdgeFilter),
    ("emboss", new EmbossFilter),
    ("error_diffusion_halftone", new ErrorDiffusionHalftoneFilter()),
    ("gain_bias", new GainBiasFilter(0.5f, 0.5f)),
    ("gamma", new GammaFilter(2)),
    ("gaussian", new GaussianBlurFilter()),
    ("glow", new GlowFilter()),
    ("gotham", new GothamFilter()),
    ("grayscale", new GrayscaleFilter),
    ("hsb", new HSBFilter(0.5f, 0, 0)),
    ("invert_alpha", new InvertAlphaFilter),
    ("invert", new InvertFilter),
    ("kaleidoscope", new KaleidoscopeFilter()),
    ("lensblur", new LensBlurFilter()),
    ("lensflare", new LensFlareFilter),
    ("minimum", new MinimumFilter),
    ("maximum", new MaximumFilter),
    ("motionblur", new MotionBlurFilter(Math.PI / 3.0, 20)),
    ("nashville", new NashvilleFilter()),
    ("noise", new NoiseFilter()),
    ("offset", new OffsetFilter(60, 40)),
    ("oil", new OilFilter()),
    ("old_photo", new OldPhotoFilter()),
    ("opacity", new OpacityFilter(0.5f)),
    ("pixelate", new PixelateFilter(4)),
    ("pointillize_square", new PointillizeFilter(PointillizeGridType.Square)),
    ("posterize", new PosterizeFilter()),
    ("prewitt", new PrewittFilter),
    ("quantize", new QuantizeFilter(256)),
    ("rays", new RaysFilter(0.1f, 0.6f, 0.5f)),
    ("rgb", new RGBFilter(0.4f, 0.6f, 0.5f)),
    ("ripple", new RippleFilter(RippleType.Sine)),
    ("roberts", new RobertsFilter),
    ("rylanders", new RylandersFilter),
    ("sepia", new SepiaFilter),
    ("sharpen", new SharpenFilter),
    ("smear_circles", new SmearFilter(SmearType.Circles)),
    ("snow", new SnowFilter()),
    ("sobels", new SobelsFilter),
    ("solarize", new SolarizeFilter),
    ("sparkle", new SparkleFilter()),
    ("summer", new SummerFilter(true)),
    ("swim", new SwimFilter()),
    ("television", new TelevisionFilter),
    ("threshold", new ThresholdFilter(127)),
    ("tritone", new TritoneFilter(new java.awt.Color(0xFF000044), new java.awt.Color(0xFF0066FF), java.awt.Color.WHITE)),
    ("twirl", new TwirlFilter(75)),
    ("unsharp", new UnsharpFilter()),
    ("vignette", new VignetteFilter()),
    ("vintage", new VintageFilter),
    ("watermark_cover", new WatermarkCoverFilter("watermark", font, true, 0.2, Color.White.toAWT)),
    ("watermark_stamp", new WatermarkStampFilter("watermark", font, true, 0.2, Color.White.toAWT))
  )

  val sb = new StringBuffer()

  for (filter <- filters) {

    val filterName = filter._1

    sb.append("\n| " + filterName + " | ")

    for (t <- List(("bird", image1), ("colosseum", image2), ("lanzarote", image3))) {

      val filename = t._1
      val image = t._2
      val resized = image.scaleToWidth(1200)

      println("Generating example " + filename + " " + filterName)

      resized.filter(filter._2).output(new File("examples/filters/" + filename + "_" + filterName + "_large.jpeg"))(JpegWriter.compression(95))
      resized.filter(filter._2).scaleToWidth(250).forWriter(PngWriter.MaxCompression)
              .write(new File("examples/filters/" + filename + "_" + filterName + "_small.png"))

      sb
        .append(s"<a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/${filename}_${filterName}_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/${filename}_${filterName}_small.png'><a/> | ")
    }

    //// --- API examples /////

    //    image.pad(20, Color.Black).write(new File("examples/" + filename + "_pad_20.png"))
    //    image.resize(0.5).write(new File("examples/" + filename + "_resize_half.png"))
    //   image.fit(image.width - 20, image.height - 100).write(new File("examples/" + filename + "_fitted.png"))
    //    image.scale(0.5).write(new File("examples/" + filename + "_scale_half.png"))
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
    val fgImage = resized // image.scaleHeightToRatio(ratio)
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
