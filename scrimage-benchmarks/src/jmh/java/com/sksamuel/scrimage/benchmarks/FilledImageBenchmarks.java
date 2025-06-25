package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import net.coobird.thumbnailator.Thumbnails;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FilledImageBenchmarks {

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testFilledImage100x100_RBG(Blackhole blackhole) throws IOException {
      ImmutableImage image = ImmutableImage.filled(100, 100, Color.WHITE, BufferedImage.TYPE_INT_RGB);
      blackhole.consume(image);
   }

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testFilledImage1000x1000_ARBG(Blackhole blackhole) throws IOException {
      ImmutableImage image = ImmutableImage.filled(1000, 1000, Color.WHITE, BufferedImage.TYPE_INT_ARGB);
      blackhole.consume(image);
   }
}
