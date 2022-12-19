package com.sksamuel.scrimage.benchmarks;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import net.coobird.thumbnailator.Thumbnails;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ScaleBenchmarks {

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testScaleBicubicScrimage(Blackhole blackhole) throws IOException {
      BufferedImage resized = ImmutableImage.loader().fromResource("/bench_colosseum.jpg")
         .scaleTo(600, 400, ScaleMethod.Bicubic)
         .awt();
      blackhole.consume(resized);
   }

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testScaleProgressiveBilinearScrimage(Blackhole blackhole) throws IOException {
      BufferedImage resized = ImmutableImage.loader().fromResource("/bench_colosseum.jpg")
         .scaleTo(600, 400, ScaleMethod.Progressive)
         .awt();
      blackhole.consume(resized);
   }

   @Benchmark
   @BenchmarkMode(Mode.AverageTime)
   @OutputTimeUnit(TimeUnit.MILLISECONDS)
   public void testScaleProgressiveBilinearThumbnailator(Blackhole blackhole) throws IOException {
      BufferedImage resized = Thumbnails.of(getClass().getResourceAsStream("/bench_colosseum.jpg"))
         .size(600, 400)
         .keepAspectRatio(false)
         .asBufferedImage();
      blackhole.consume(resized);
   }
}
