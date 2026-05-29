package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for the resize/scale family of operations. "scale" changes the
 * pixel dimensions (resampling), while "resize" changes the canvas size
 * (cropping/padding around a position). The source image is decoded once per
 * trial.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ResizeBenchmarks {

   private ImmutableImage source;

   @Setup(Level.Trial)
   public void setup() throws IOException {
      source = ImmutableImage.loader().fromResource("/bench_bird.jpg");
   }

   @Benchmark
   public void scaleHalfBicubic(Blackhole blackhole) {
      blackhole.consume(source.scale(0.5, ScaleMethod.Bicubic));
   }

   @Benchmark
   public void scaleHalfFastScale(Blackhole blackhole) {
      blackhole.consume(source.scale(0.5, ScaleMethod.FastScale));
   }

   @Benchmark
   public void scaleToWidth(Blackhole blackhole) {
      blackhole.consume(source.scaleToWidth(source.width / 2));
   }

   @Benchmark
   public void resizeCanvasHalf(Blackhole blackhole) {
      blackhole.consume(source.resize(0.5));
   }

   @Benchmark
   public void cover(Blackhole blackhole) {
      blackhole.consume(source.cover(source.width / 2, source.height / 2));
   }

   @Benchmark
   public void fit(Blackhole blackhole) {
      blackhole.consume(source.fit(source.width / 2, source.height / 2));
   }

   @Benchmark
   public void bound(Blackhole blackhole) {
      blackhole.consume(source.bound(source.width / 2, source.height / 2));
   }

   @Benchmark
   public void max(Blackhole blackhole) {
      blackhole.consume(source.max(source.width / 2, source.height / 2));
   }

   @Benchmark
   public void zoom(Blackhole blackhole) {
      blackhole.consume(source.zoom(1.5));
   }
}
