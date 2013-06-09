package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object ExampleGenerator extends App {

    val image1 = Image(getClass.getResourceAsStream("/bird.jpg"))
    val image2 = Image(getClass.getResourceAsStream("/colosseum.jpg"))
    val image3 = Image(getClass.getResourceAsStream("/lanzarote.jpg"))

    val filters: List[(String, Filter)] = List(
        ("blur", BlurFilter),
        ("brightness", BrightnessFilter(1.3f)),
        ("bump", BumpFilter),
        ("chrome", ChromeFilter()),
        ("color_halftone", ColorHalftoneFilter()),
        ("contour", ContourFilter()),
        ("contrast", ContrastFilter(1.3f)),
        ("despeckle", DespeckleFilter),
        ("diffuse_4", DiffuseFilter(4)),
        ("dither", DitherFilter),
        ("edge", EdgeFilter),
        ("emboss", EmbossFilter),
        ("errordiffusion", ErrorDiffusionHalftoneFilter()),
        ("gamma_2", GammaFilter(2)),
        ("gaussian", GaussianBlurFilter()),
        ("glow", GlowFilter()),
        ("grayscale", GrayscaleFilter),
        ("invert", InvertFilter),
        ("lensblur", LensBlurFilter()),
        ("lensflare", LensFlareFilter),
        ("minimum", MinimumFilter),
        ("maximum", MaximumFilter),
        ("offset", OffsetFilter(60, 40)),
        ("oil", OilFilter()),
        ("pixelate_4", PixelateFilter(4)),
        ("pointillize_square", PointillizeFilter(PointillizeGridType.Square)),
        ("posterize", PosterizeFilter()),
        ("prewitt", PrewittFilter),
        ("quantize_256", QuantizeFilter(256)),
        ("rays", RaysFilter(threshold = 0.1f, strength = 0.6f)),
        ("ripple", RippleFilter(RippleType.Sine)),
        ("roberts", RobertsFilter),
        ("rylanders", RylandersFilter),
        ("sepia", SepiaFilter),
        ("smear_circles", SmearFilter(SmearType.Circles)),
        ("sobels", SobelsFilter),
        ("solarize", SolarizeFilter),
        ("sparkle", SparkleFilter()),
        ("swim", SwimFilter()),
        ("television", TelevisionFilter),
        ("threshold_127", ThresholdFilter(127)),
        ("twirl", TwirlFilter(75)),
        ("unsharp", UnsharpFilter()),
        ("vintage", VintageFilter))

    for ( t <- List(("bird", image1), ("colosseum", image2), ("lanzarote", image3)) ) {

        val filename = t._1
        val image = t._2
        val large = image.scaleToWidth(1200)
        val small = image.scaleToWidth(200)

        for ( filter <- filters ) {
            val filterName = filter._1
            println("Generating example " + filename + " " + filterName)
            large.filter(filter._2).write(new File("examples/filters/" + filename + "_" + filterName + "_large.jpeg"), Format.JPEG)
            small.filter(filter._2).writer(Format.PNG).withCompression(9)
              .write(new File("examples/filters/" + filename + "_" + filterName + "_small.png"))
        }

        //// --- API examples /////

        //    image.pad(20, Color.Black).write(new File("examples/" + filename + "_pad_20.png"))
        //    image.resize(0.5).write(new File("examples/" + filename + "_resize_half.png"))
        //   image.fit(image.width - 20, image.height - 100).write(new File("examples/" + filename + "_fitted.png"))
        //    image.scale(0.5).write(new File("examples/" + filename + "_scale_half.png"))
    }
}
