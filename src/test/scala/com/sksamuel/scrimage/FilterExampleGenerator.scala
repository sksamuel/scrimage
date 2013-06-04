package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object FilterExampleGenerator extends App {

    val image = Image(getClass.getResourceAsStream("/bird.jpg")).resize(0.5)

    image.filter(SolarizeFilter).write(new File("examples/filters/bird_solarize.png"))
    image.filter(EdgeFilter).write(new File("examples/filters/bird_edge.png"))
    image.filter(DespeckleFilter).write(new File("examples/filters/bird_despeckle.png"))
    image.filter(GaussianBlurFilter()).write(new File("examples/filters/bird_gaussian.png"))
    image.filter(LensBlurFilter()).write(new File("examples/filters/bird_lensblur.png"))
    image.filter(RippleFilter(RippleType.Triangle)).write(new File("examples/filters/bird_ripple.png"))
    image.filter(SmearFilter(SmearType.Lines)).write(new File("examples/filters/bird_smear.png"))
}
