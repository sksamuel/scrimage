package com.sksamuel.scrimage

import com.sksamuel.scrimage.filter._
import java.io.File

/** @author Stephen Samuel */
object FilterExampleGenerator extends App {

    val image = Image(getClass.getResourceAsStream("/bird.jpg")).scale(0.5)

    image.filter(SolarizeFilter).write(new File("examples/filters/bird_solarize.png"))
    image.filter(EdgeFilter).write(new File("examples/filters/bird_edge.png"))
    image.filter(DespeckleFilter).write(new File("examples/filters/bird_despeckle.png"))
    image.filter(GaussianBlurFilter()).write(new File("examples/filters/bird_gaussian.png"))
    image.filter(LensBlurFilter()).write(new File("examples/filters/bird_lensblur.png"))
    image.filter(RippleFilter(RippleType.Sine)).write(new File("examples/filters/bird_ripple.png"))
    image.filter(SmearFilter(SmearType.Lines)).write(new File("examples/filters/bird_smear.png"))

    image.filter(PointillizeFilter(PointillizeGridType.Hexagonal)).write(new File("examples/filters/bird_pointillize_hexagon.png"))
    image.filter(PointillizeFilter(PointillizeGridType.Square)).write(new File("examples/filters/bird_pointillize_square.png"))
    image.filter(PointillizeFilter(PointillizeGridType.Triangular)).write(new File("examples/filters/bird_pointillize_triangular.png"))
    image.filter(PointillizeFilter(PointillizeGridType.Octangal)).write(new File("examples/filters/bird_pointillize_octagonal.png"))
}
