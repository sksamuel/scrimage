package com.sksamuel.scrimage.composite

import com.sksamuel.scrimage.{Format, Composite, Image}
import java.io.File
import org.apache.commons.io.FileUtils

/** @author Stephen Samuel */
object CompositeExampleGenerator extends App {

  val composites: List[(String, (Double => Composite))] = List(
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
    ("normal", new NormalComposite(_)),
    ("overlay", new OverlayComposite(_)),
    ("red", new RedComposite(_)),
    ("reflect", new ReflectComposite(_)),
    ("saturation", new SaturationComposite(_)),
    ("screen", new ScreenComposite(_)),
    ("subtract", new SubtractComposite(_))
  )

  val l1 = Image(getClass.getResourceAsStream("/mailbox.jpg")).scaleTo(1200, 800)
  val l2 = Image(getClass.getResourceAsStream("/palm.jpg")).scaleTo(1200, 800)
  val s1 = l1.scaleToWidth(300)
  val s2 = l2.scaleToWidth(300)

  val sb = new StringBuffer()
  for ( (name, composite) <- composites ) {

    sb.append("\n| " + name + " | ")

    for ( alpha <- List(0.5, 1.0) ) {

      val large = l1.composite(composite(alpha), l2)
      val small = s1.composite(composite(alpha), s2)

      println(s"Generating example [$name ($alpha)]")
      large.write(new File(s"examples/composite/${name}_${alpha}_large.jpeg"), Format.JPEG)
      small.writer(Format.PNG).withMaxCompression.write(new File(s"examples/composite/${name}_${alpha}_small.png"))

      sb.append(
        s"<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/${name}_${alpha}_large.jpeg'>")
      sb.append(
        s"<img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/${name}_${alpha}_small.png'><a/> |")
    }
  }

  FileUtils.write(new File("composite.md"), sb.toString)
}

