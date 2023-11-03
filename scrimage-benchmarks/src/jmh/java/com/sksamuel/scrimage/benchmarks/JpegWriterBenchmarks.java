package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.JpegWriter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JpegWriterBenchmarks {

   private static ImmutableImage loadImageFromResource(String resourceName) {
      try {
         return ImmutableImage.loader().fromResource(resourceName);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @State(value = Scope.Benchmark)
   public static class ReadImages {
      ImmutableImage opaqueImage = loadImageFromResource("/bench_bird.jpg");
      ImmutableImage transparentImage = loadImageFromResource("/bench_3.png");

      ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024);
   }

   public static class Reading {
      @Benchmark
      @BenchmarkMode(Mode.AverageTime)
      @OutputTimeUnit(TimeUnit.MILLISECONDS)
      public void testScrimageJpegWriterDefault(Blackhole blackhole) throws IOException {
         ImmutableImage image = ImmutableImage.loader().fromResource("/bench_bird.jpg");
         blackhole.consume(image);
      }
   }


   public static class Writing {

      @Benchmark
      @BenchmarkMode(Mode.AverageTime)
      @OutputTimeUnit(TimeUnit.MILLISECONDS)
      public void testJpegWritingOfTransparentImage(Blackhole blackhole, ReadImages state) throws IOException {
         ImmutableImage image = state.transparentImage;
         image.forWriter(JpegWriter.Default).write(state.out);
         blackhole.consume(state.out);
         state.out.reset();
      }

      @Benchmark
      @BenchmarkMode(Mode.AverageTime)
      @OutputTimeUnit(TimeUnit.MILLISECONDS)
      public void testJpegWritingOfOpaqueImage(Blackhole blackhole, ReadImages state) throws IOException {
         ImmutableImage image = state.opaqueImage;
         image.forWriter(JpegWriter.Default).write(state.out);
         blackhole.consume(state.out);
         state.out.reset();
      }
   }


}
