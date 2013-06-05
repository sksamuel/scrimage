package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object FilterExampleGenerator extends App {

    val image = Image(getClass.getResourceAsStream("/bird.jpg")).scale(0.15)

    image.filter(BlockFilter(4)).write(new File("examples/filters/bird_block_4.png"))
    image.filter(BlurFilter).write(new File("examples/filters/bird_blur.png"))
    image.filter(BumpFilter).write(new File("examples/filters/bird_bump.png"))

    image.filter(ChromeFilter()).write(new File("examples/filters/bird_chrome.png"))

    image.filter(DespeckleFilter).write(new File("examples/filters/bird_despeckle.png"))

    image.filter(EdgeFilter).write(new File("examples/filters/bird_edge.png"))

    image.filter(GaussianBlurFilter()).write(new File("examples/filters/bird_gaussian.png"))

    image.filter(InvertFilter).write(new File("examples/filters/bird_invert.png"))

    image.filter(LensBlurFilter()).write(new File("examples/filters/bird_lensblur.png"))

    image.filter(QuantizeFilter(256)).write(new File("examples/filters/bird_quantize_256.png"))

    image.filter(PointillizeFilter(PointillizeGridType.Square)).write(new File("examples/filters/bird_pointillize_square.png"))

    image.filter(RippleFilter(RippleType.Sine)).write(new File("examples/filters/bird_ripple.png"))

    image.filter(SmearFilter(SmearType.Circles)).write(new File("examples/filters/bird_smear_circles.png"))
    image.filter(SolarizeFilter).write(new File("examples/filters/bird_solarize.png"))

    image.filter(ThresholdFilter(127, black = 0x00ff0000)).write(new File("examples/filters/bird_threshold_red_127.png"))

    image.filter(UnsharpFilter()).write(new File("examples/filters/bird_unsharp.png"))

}
