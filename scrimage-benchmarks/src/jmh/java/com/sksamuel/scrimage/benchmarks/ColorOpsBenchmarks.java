package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.color.GrayscaleMethod;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for per-pixel colour operations (grayscale, brightness, contrast
 * and a custom map). The source image is decoded once per trial.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ColorOpsBenchmarks {

   private ImmutableImage source;

   @Setup(Level.Trial)
   public void setup() throws IOException {
      source = ImmutableImage.loader().fromResource("/bench_bird.jpg");
   }

   @Benchmark
   public void grayscaleAverage(Blackhole blackhole) {
      blackhole.consume(source.toGrayscale(GrayscaleMethod.AVERAGE));
   }

   @Benchmark
   public void grayscaleLuma(Blackhole blackhole) {
      blackhole.consume(source.toGrayscale(GrayscaleMethod.LUMA));
   }

   @Benchmark
   public void grayscaleWeighted(Blackhole blackhole) {
      blackhole.consume(source.toGrayscale(GrayscaleMethod.WEIGHTED));
   }

   @Benchmark
   public void brightness(Blackhole blackhole) {
      blackhole.consume(source.brightness(1.2));
   }

   @Benchmark
   public void contrast(Blackhole blackhole) {
      blackhole.consume(source.contrast(1.2));
   }

   @Benchmark
   public void mapInvert(Blackhole blackhole) {
      blackhole.consume(source.map(p -> new Color(255 - p.red(), 255 - p.green(), 255 - p.blue())));
   }
}
