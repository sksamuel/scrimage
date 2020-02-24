package com.sksamuel.scrimage.scala

import java.awt.Color
import java.io.File
import java.nio.file.Path

import com.sksamuel.scrimage.{ImageWriter, ImmutableImage}
import com.sksamuel.scrimage.pixels.Pixel

import scala.language.implicitConversions

object implicits {
   implicit def image2rich(image: ImmutableImage): RichImmutableImage = new RichImmutableImage(image)
}

class RichImmutableImage(image: ImmutableImage) {

   def map(f: Pixel => Color): ImmutableImage = {
      val fn: java.util.function.Function[Pixel, Color] = (t: Pixel) => f(t)
      image.map(fn)
   }

   def forall(f: Pixel => Boolean): Boolean = {
      val fn: java.util.function.Predicate[Pixel] = (t: Pixel) => f(t)
      image.forAll(fn)
   }

   def foreach(f: Pixel => Unit): Unit = {
      val fn: java.util.function.Consumer[Pixel] = (t: Pixel) => f(t)
      image.forEach(fn)
   }

   def output(file: File)(implicit writer: ImageWriter): Unit = {
      image.output(writer, file)
   }

   def output(path: Path)(implicit writer: ImageWriter): Unit = {
      image.output(writer, path)
   }

   def output(path: String)(implicit writer: ImageWriter): Unit = {
      image.output(writer, path)
   }
}
