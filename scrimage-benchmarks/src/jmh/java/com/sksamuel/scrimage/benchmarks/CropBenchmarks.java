package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
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
 * Benchmarks for the crop family of operations. The source image is decoded
 * once per trial so the measurements reflect the operation itself rather than
 * the JPEG decode.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CropBenchmarks {

   private ImmutableImage source;

   @Setup(Level.Trial)
   public void setup() throws IOException {
      source = ImmutableImage.loader().fromResource("/bench_bird.jpg");
   }

   @Benchmark
   public void subimage(Blackhole blackhole) {
      blackhole.consume(source.subimage(source.width / 4, source.height / 4, source.width / 2, source.height / 2));
   }

   @Benchmark
   public void trim(Blackhole blackhole) {
      blackhole.consume(source.trim(source.width / 8));
   }

   @Benchmark
   public void takeLeft(Blackhole blackhole) {
      blackhole.consume(source.takeLeft(source.width / 2));
   }

   @Benchmark
   public void takeTop(Blackhole blackhole) {
      blackhole.consume(source.takeTop(source.height / 2));
   }

   @Benchmark
   public void autocrop(Blackhole blackhole) {
      blackhole.consume(source.autocrop());
   }

   @Benchmark
   public void subpixelSubimage(Blackhole blackhole) {
      blackhole.consume(source.subpixelSubimage(0.5, 0.5, source.width / 2, source.height / 2));
   }
}
