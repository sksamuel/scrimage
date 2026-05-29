package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.Position;
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
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for canvas/compositing operations (pad, fill, overlay, underlay,
 * removeTransparency and a generic BufferedImageOp). The source image and a
 * half-size translucent overlay are built once per trial.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CompositeBenchmarks {

   private ImmutableImage source;
   private ImmutableImage overlay;

   @Setup(Level.Trial)
   public void setup() throws IOException {
      source = ImmutableImage.loader().fromResource("/bench_bird.jpg");
      overlay = ImmutableImage.filled(source.width / 2, source.height / 2, new Color(255, 0, 0, 128), BufferedImage.TYPE_INT_ARGB);
   }

   @Benchmark
   public void pad(Blackhole blackhole) {
      blackhole.consume(source.pad(50, Color.BLACK));
   }

   @Benchmark
   public void padTo(Blackhole blackhole) {
      blackhole.consume(source.padTo(source.width + 200, source.height + 200));
   }

   @Benchmark
   public void fill(Blackhole blackhole) {
      blackhole.consume(source.fill(Color.BLUE));
   }

   @Benchmark
   public void overlay(Blackhole blackhole) {
      blackhole.consume(source.overlay(overlay, Position.Center));
   }

   @Benchmark
   public void underlay(Blackhole blackhole) {
      blackhole.consume(source.underlay(overlay));
   }

   @Benchmark
   public void removeTransparency(Blackhole blackhole) {
      blackhole.consume(source.removeTransparency(Color.WHITE));
   }

   @Benchmark
   public void op(Blackhole blackhole) {
      blackhole.consume(source.op(new RescaleOp(1.2f, 0f, null)));
   }
}
