package com.sksamuel.scrimage.composite

import java.io.File

import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.{ Composite, Image }
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
object CompositeExampleGenerator extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val composites: List[(String, (Double => Composite))] = List(
    ("alpha", new AlphaComposite(_)),
    ("average", new AverageComposite(_)),
    ("blue", new BlueComposite(_)),
    ("color", new ColorComposite(_)),
    ("colorburn", new ColorBurnComposite(_)),
    ("colordodge", new ColorDodgeComposite(_)),
    ("diff", new DifferenceComposite(_)),
    ("green", new GreenComposite(_)),
    ("grow", new GlowComposite(_)),
    ("hue", new HueComposite(_)),
    ("hard", new HardLightComposite(_)),
    ("heat", new HeatComposite(_)),
    ("lighten", new LightenComposite(_)),
    ("negation", new NegationComposite(_)),
    ("luminosity", new LuminosityComposite(_)),
    ("multiply", new MultiplyComposite(_)),
    ("negation", new NegationComposite(_)),
    ("overlay", new OverlayComposite(_)),
    ("red", new RedComposite(_)),
    ("reflect", new ReflectComposite(_)),
    ("saturation", new SaturationComposite(_)),
    ("screen", new ScreenComposite(_)),
    ("subtract", new SubtractComposite(_))
  )

  val l1 = Image(getClass.getResourceAsStream("/mailbox.jpg")).scaleTo(1200, 800)
  val l2 = Image(getClass.getResourceAsStream("/palm.jpg")).scaleTo(1200, 800)
  val s1 = l1.scaleToWidth(200)
  val s2 = l2.scaleToWidth(200)

  val sb = new StringBuffer()
  for ((name, composite) <- composites) {

    sb.append("\n| " + name + " | ")

    for (alpha <- List(0.5, 1.0)) {

      val large = l1.composite(composite(alpha), l2)
      val small = s1.composite(composite(alpha), s2)

      println(s"Generating example [$name ($alpha)]")
      large.output(new File(s"examples/composite/${name}_${alpha}_large.jpeg"))(JpegWriter.NoCompression)
      small.output(new File(s"examples/composite/${name}_${alpha}_small.png"))

      sb.append(
        s"<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/${name}_${alpha}_large.jpeg'>")
      sb.append(
        s"<img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/${name}_${alpha}_small.png'><a/> |")
    }
  }

  FileUtils.write(new File("composite.md"), sb.toString)
}

