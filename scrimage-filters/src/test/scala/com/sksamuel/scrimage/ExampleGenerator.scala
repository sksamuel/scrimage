package com.sksamuel.scrimage

import java.io.File

import com.sksamuel.scrimage.filter._
import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import org.apache.commons.io.FileUtils

object ExampleGenerator extends App {


  val image1 = Image.fromResource("/bird.jpg")
  val image2 = Image.fromResource("/colosseum.jpg")
  val image3 = Image.fromResource("/lanzarote.jpg")

  val filters: List[(String, Filter)] = List(
    ("blur", new BlurFilter),
    ("border", BorderFilter(8)),
    ("brightness", new BrightnessFilter(1.3f)),
    ("bump", new BumpFilter),
    ("chrome", new ChromeFilter()),
    ("color_halftone", new ColorHalftoneFilter()),
    ("contour", new ContourFilter()),
    ("contrast", new ContrastFilter(1.3f)),
    ("despeckle", new DespeckleFilter),
    ("diffuse", new DiffuseFilter(4)),
    ("dither", new DitherFilter),
    ("edge", new EdgeFilter),
    ("emboss", new EmbossFilter),
    ("errordiffusion", new ErrorDiffusionHalftoneFilter()),
    ("gamma", new GammaFilter(2)),
    ("gaussian", new GaussianBlurFilter()),
    ("glow", new GlowFilter()),
    ("grayscale", GrayscaleFilter),
    ("hsb", HSBFilter(0.5)),
    ("invert", new InvertFilter),
    ("lensblur", new LensBlurFilter()),
    ("lensflare", new LensFlareFilter),
    ("minimum", MinimumFilter),
    ("maximum", MaximumFilter),
    ("motionblur", new MotionBlurFilter(Math.PI / 3.0, 20)),
    ("noise", new NoiseFilter()),
    ("offset", new OffsetFilter(60, 40)),
    ("oil", new OilFilter()),
    ("pixelate", PixelateFilter(4)),
    ("pointillize_square", PointillizeFilter(PointillizeGridType.Square)),
    ("posterize", PosterizeFilter()),
    ("prewitt", new PrewittFilter),
    ("quantize", new QuantizeFilter(256)),
    ("rays", RaysFilter(threshold = 0.1f, strength = 0.6f)),
    ("ripple", new RippleFilter(RippleType.Sine)),
    ("roberts", new RobertsFilter),
    ("rylanders", new RylandersFilter),
    ("sepia", new SepiaFilter),
    ("smear_circles", new SmearFilter(SmearType.Circles)),
    ("snow", new SnowFilter()),
    ("sobels", new SobelsFilter),
    ("solarize", new SolarizeFilter),
    ("sparkle", new SparkleFilter()),
    ("summer", SummerFilter()),
    ("swim", SwimFilter()),
    ("television", TelevisionFilter),
    ("threshold", ThresholdFilter(127)),
    ("tritone", TritoneFilter(new java.awt.Color(0xFF000044), new java.awt.Color(0xFF0066FF), java.awt.Color.WHITE)),
    ("twirl", new TwirlFilter(75)),
    ("unsharp", UnsharpFilter()),
    ("vignette", VignetteFilter()),
    ("vintage", VintageFilter))

  val sb = new StringBuffer()

  for (filter <- filters) {

    val filterName = filter._1

    sb.append("\n| " + filterName + " | ")

    for (t <- List(("bird", image1), ("colosseum", image2), ("lanzarote", image3))) {

      val filename = t._1
      val image = t._2
      val large = image.scaleToWidth(1200)
      val small = image.scaleToWidth(200)

      println("Generating example " + filename + " " + filterName)
      large.filter(filter._2).output(new File("examples/filters/" + filename + "_" + filterName + "_large.jpeg"))(JpegWriter.NoCompression)
      small.filter(filter._2).forWriter(PngWriter.MaxCompression)
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

  FileUtils.write(new File("filters.md"), sb.toString)
}
