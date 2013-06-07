package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object ExampleGenerator extends App {

    val image1 = Image(getClass.getResourceAsStream("/bird.jpg")).scaleToWidth(260)
    val image2 = Image(getClass.getResourceAsStream("/colosseum.jpg")).scaleToWidth(260)
    val image3 = Image(getClass.getResourceAsStream("/lanzarote.jpg")).scaleToWidth(260)

    for ( t <- List(("bird", image1), ("colosseum", image2), ("lanzarote", image3)) ) {

        val filename = t._1
        val image = t._2

        image.filter(BlockFilter(4)).write(new File("examples/filters/" + filename + "_block_4.png"))
        image.filter(BlurFilter).write(new File("examples/filters/" + filename + "_blur.png"))
        image.filter(BumpFilter).write(new File("examples/filters/" + filename + "_bump.png"))

        image.filter(ChromeFilter()).write(new File("examples/filters/" + filename + "_chrome.png"))

        image.filter(DespeckleFilter).write(new File("examples/filters/" + filename + "_despeckle.png"))
        image.filter(DitherFilter).write(new File("examples/filters/" + filename + "_dither.png"))
        image.filter(DiffuseFilter(4)).write(new File("examples/filters/" + filename + "_diffuse_4.png"))

        image.filter(EdgeFilter).write(new File("examples/filters/" + filename + "_edge.png"))
        image.filter(ErrorDiffusionHalftoneFilter()).write(new File("examples/filters/" + filename + "_errordiffusion.png"))

        image.filter(GammaFilter(2)).write(new File("examples/filters/" + filename + "_gamma_2.png"))
        image.filter(GaussianBlurFilter()).write(new File("examples/filters/" + filename + "_gaussian.png"))
        image.filter(GlowFilter()).write(new File("examples/filters/" + filename + "_glow.png"))
        image.filter(GrayscaleFilter).write(new File("examples/filters/" + filename + "_grayscale.png"))

        image.filter(InvertFilter).write(new File("examples/filters/" + filename + "_invert.png"))

        image.filter(LensBlurFilter()).write(new File("examples/filters/" + filename + "_lensblur.png"))
        image.filter(LensFlareFilter).write(new File("examples/filters/" + filename + "_lensflare.png"))

        image.filter(OilFilter()).write(new File("examples/filters/" + filename + "_oil.png"))

        image
          .filter(PointillizeFilter(PointillizeGridType.Square))
          .write(new File("examples/filters/" + filename + "_pointillize_square.png"))

        image.filter(QuantizeFilter(256)).write(new File("examples/filters/" + filename + "_quantize_256.png"))

        image.filter(RaysFilter(threshold = 0.1f, strength = 0.6f)).write(new File("examples/filters/" + filename + "_rays.png"))
        image.filter(RippleFilter(RippleType.Sine)).write(new File("examples/filters/" + filename + "_ripple.png"))
        image.filter(RylandersFilter).write(new File("examples/filters/" + filename + "_rylanders.png"))

        image.filter(SmearFilter(SmearType.Circles)).write(new File("examples/filters/" + filename + "_smear_circles.png"))
        image.filter(SparkleFilter()).write(new File("examples/filters/" + filename + "_sparkle.png"))
        image.filter(SolarizeFilter).write(new File("examples/filters/" + filename + "_solarize.png"))

        image.filter(TelevisionFilter).write(new File("examples/filters/" + filename + "_television.png"))
        image.filter(ThresholdFilter(127)).write(new File("examples/filters/" + filename + "_threshold_red_127.png"))

        image.filter(UnsharpFilter()).write(new File("examples/filters/" + filename + "_unsharp.png"))

        //// --- API examples /////

        image.pad(20, Color.Black).write(new File("examples/" + filename + "_pad_20.png"))
        image.resize(0.5).write(new File("examples/" + filename + "_resize_half.png"))
        image.fit(image.width - 20, image.height - 100).write(new File("examples/" + filename + "_fitted.png"))
        image.scale(0.5).write(new File("examples/" + filename + "_scale_half.png"))
    }
}
