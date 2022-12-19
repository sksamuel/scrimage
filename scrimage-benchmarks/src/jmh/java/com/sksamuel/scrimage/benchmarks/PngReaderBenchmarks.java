package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.format.png.PngReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PngReaderBenchmarks {

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testScrimagePngReads(Blackhole blackhole) throws IOException {
      ImmutableImage image1 = new PngReader().read(this.getClass().getResourceAsStream("/bench_1.png"));
      ImmutableImage image2 = new PngReader().read(this.getClass().getResourceAsStream("/bench_2.png"));
      ImmutableImage image3 = new PngReader().read(this.getClass().getResourceAsStream("/bench_3.png"));
      blackhole.consume(image1);
      blackhole.consume(image2);
      blackhole.consume(image3);
   }

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testImageIOPngReads(Blackhole blackhole) throws IOException {
      ImmutableImage image1 = ImmutableImage.loader().fromResource("/bench_1.png");
      ImmutableImage image2 = ImmutableImage.loader().fromResource("/bench_2.png");
      ImmutableImage image3 = ImmutableImage.loader().fromResource("/bench_3.png");
      blackhole.consume(image1);
      blackhole.consume(image2);
      blackhole.consume(image3);
   }
}
