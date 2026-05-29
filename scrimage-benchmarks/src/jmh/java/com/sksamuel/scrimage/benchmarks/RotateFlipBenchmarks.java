package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.angles.Degrees;
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
 * Benchmarks for rotation, flipping and translation. The source image is
 * decoded once per trial.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RotateFlipBenchmarks {

   private ImmutableImage source;

   @Setup(Level.Trial)
   public void setup() throws IOException {
      source = ImmutableImage.loader().fromResource("/bench_bird.jpg");
   }

   @Benchmark
   public void rotateLeft(Blackhole blackhole) {
      blackhole.consume(source.rotateLeft());
   }

   @Benchmark
   public void rotateRight(Blackhole blackhole) {
      blackhole.consume(source.rotateRight());
   }

   @Benchmark
   public void rotate45Degrees(Blackhole blackhole) {
      blackhole.consume(source.rotate(new Degrees(45)));
   }

   @Benchmark
   public void flipX(Blackhole blackhole) {
      blackhole.consume(source.flipX());
   }

   @Benchmark
   public void flipY(Blackhole blackhole) {
      blackhole.consume(source.flipY());
   }

   @Benchmark
   public void translate(Blackhole blackhole) {
      blackhole.consume(source.translate(50, 50));
   }
}
