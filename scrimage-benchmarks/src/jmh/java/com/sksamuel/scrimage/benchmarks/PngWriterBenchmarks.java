package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.PngWriter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PngWriterBenchmarks {

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testScrimagePngWriterNoCompression(Blackhole blackhole) throws IOException {
      ImmutableImage image = ImmutableImage.loader().fromResource("/bench_bird.jpg");
      byte[] png = image.bytes(PngWriter.NoCompression);
      blackhole.consume(png);
   }
}
