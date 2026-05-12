package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.NoSuchElementException

/**
 * Regression test for AwtImage.iterator() honouring the Iterator contract.
 *
 * The Iterator interface specifies that next() "throws
 * NoSuchElementException - if the iteration has no more elements". The
 * previous implementation incremented its index unconditionally and on
 * exhaustion threw ArrayIndexOutOfBoundsException from the int[] read.
 *
 * Code that catches NoSuchElementException defensively (or composes
 * iterators with helpers that rely on the contract) would silently miss
 * the exhaustion.
 */
class IteratorContractTest : FunSpec({

   test("iterator().next() throws NoSuchElementException when exhausted") {
      val pixels = arrayOf(
         Pixel(0, 0, 255, 0, 0, 255),
         Pixel(1, 0, 0, 255, 0, 255)
      )
      val image = ImmutableImage.create(2, 1, pixels)
      val it = image.iterator()
      // Drain the iterator
      it.next(); it.next()
      it.hasNext() shouldBe false
      shouldThrow<NoSuchElementException> { it.next() }
   }

   test("iterator() yields the expected pixel count then reports false") {
      val image = ImmutableImage.create(3, 2)
      val it = image.iterator()
      var count = 0
      while (it.hasNext()) { it.next(); count++ }
      count shouldBe 6
      it.hasNext() shouldBe false
   }

   test("iterator() walks pixels in row-major order") {
      val pixels = Array(6) { i -> Pixel(i % 3, i / 3, 0xFF000000.toInt() or i) }
      val image = ImmutableImage.create(3, 2, pixels)
      val collected = image.iterator().asSequence().toList()
      collected.size shouldBe 6
      for (i in 0 until 6) {
         collected[i].x shouldBe (i % 3)
         collected[i].y shouldBe (i / 3)
         (collected[i].argb and 0xFF) shouldBe i
      }
   }
})
