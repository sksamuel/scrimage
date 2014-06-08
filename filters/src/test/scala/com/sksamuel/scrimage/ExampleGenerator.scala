package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File
import org.apache.commons.io.FileUtils
import java.awt.Color

/** @author Stephen Samuel */
object ExampleGenerator extends App {

  val image1 = Image(getClass.getResourceAsStream("/bird.jpg"))
  val image2 = Image(getClass.getResourceAsStream("/colosseum.jpg"))
  val image3 = Image(getClass.getResourceAsStream("/lanzarote.jpg"))

  val filters: List[(String, Filter)] = List(
    ("blur", BlurFilter),
    ("border", BorderFilter(8)),
    ("brightness", BrightnessFilter(1.3f)),
    ("bump", BumpFilter),
    ("chrome", ChromeFilter()),
    ("color_halftone", ColorHalftoneFilter()),
    ("contour", ContourFilter()),
    ("contrast", ContrastFilter(1.3f)),
    ("despeckle", DespeckleFilter),
    ("diffuse", DiffuseFilter(4)),
    ("dither", DitherFilter),
    ("edge", EdgeFilter),
    ("emboss", EmbossFilter),
    ("errordiffusion", ErrorDiffusionHalftoneFilter()),
    ("gamma", GammaFilter(2)),
    ("gaussian", GaussianBlurFilter()),
    ("glow", GlowFilter()),
    ("grayscale", GrayscaleFilter),
    ("hsb", HSBFilter(0.5)),
    ("invert", InvertFilter),
    ("lensblur", LensBlurFilter()),
    ("lensflare", LensFlareFilter),
    ("minimum", MinimumFilter),
    ("maximum", MaximumFilter),
    ("motionblur", MotionBlurFilter(Math.PI / 3.0, 20)),
    ("noise", NoiseFilter()),
    ("offset", OffsetFilter(60, 40)),
    ("oil", OilFilter()),
    ("pixelate", PixelateFilter(4)),
    ("pointillize_square", PointillizeFilter(PointillizeGridType.Square)),
    ("posterize", PosterizeFilter()),
    ("prewitt", PrewittFilter),
    ("quantize", QuantizeFilter(256)),
    ("rays", RaysFilter(threshold = 0.1f, strength = 0.6f)),
    ("ripple", RippleFilter(RippleType.Sine)),
    ("roberts", RobertsFilter),
    ("rylanders", RylandersFilter),
    ("sepia", SepiaFilter),
    ("smear_circles", SmearFilter(SmearType.Circles)),
    ("snow", SnowFilter),
    ("sobels", SobelsFilter),
    ("solarize", SolarizeFilter),
    ("sparkle", SparkleFilter()),
    ("summer", SummerFilter()),
    ("swim", SwimFilter()),
    ("television", TelevisionFilter),
    ("threshold", ThresholdFilter(127)),
    ("tritone", TritoneFilter(new java.awt.Color(0xFF000044), new java.awt.Color(0xFF0066FF), java.awt.Color.WHITE)),
    ("twirl", TwirlFilter(75)),
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
      large.filter(filter._2).write(new File("examples/filters/" + filename + "_" + filterName + "_large.jpeg"), Format.JPEG)
      small.filter(filter._2).writer(Format.PNG).withCompression(9)
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
