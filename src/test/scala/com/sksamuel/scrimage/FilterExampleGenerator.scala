package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object FilterExampleGenerator extends App {

    val image = Image(getClass.getResourceAsStream("/bird.jpg")).scale(0.25)

    image.filter(SolarizeFilter).write(new File("examples/filters/bird_solarize.png"))

    image.filter(DespeckleFilter).write(new File("examples/filters/bird_despeckle.png"))
    image.filter(GaussianBlurFilter()).write(new File("examples/filters/bird_gaussian.png"))
    image.filter(LensBlurFilter()).write(new File("examples/filters/bird_lensblur.png"))
    image.filter(RippleFilter(RippleType.Sine)).write(new File("examples/filters/bird_ripple.png"))


    image.filter(BlockFilter(4)).write(new File("examples/filters/bird_block.png"))

    image.filter(EdgeFilter).write(new File("examples/filters/bird_edge.png"))

    image.filter(ChromeFilter()).write(new File("examples/filters/bird_chrome.png"))

    image.filter(PointillizeFilter(PointillizeGridType.Square)).write(new File("examples/filters/bird_pointillize_square.png"))

    image.filter(SmearFilter(SmearType.Lines)).write(new File("examples/filters/bird_smear.png"))

    image.filter(ThresholdFilter(127)).write(new File("examples/filters/bird_pointillize_threshold_127.png"))
    image.filter(ThresholdFilter(127, black = 0x00ff0000)).write(new File("examples/filters/bird_pointillize_threshold_red_127.png"))

    image.filter(UnsharpFilter()).write(new File("examples/filters/bird_unsharp.png"))

}
