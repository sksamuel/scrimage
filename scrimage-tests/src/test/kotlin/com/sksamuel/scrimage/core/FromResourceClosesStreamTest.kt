package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.FilterInputStream
import java.io.InputStream

/**
 * Regression test for the resource leak in [ImmutableImageLoader.fromResource], which
 * previously opened the resource [InputStream] via getClass().getResourceAsStream and
 * passed it to fromStream without ever closing it, leaking the underlying file/jar handle
 * on every call. fromStream's contract is that the caller closes the stream, and the read
 * path only reads (never closes), so fromResource must close the stream it opens.
 *
 * The loader resolves a resource through the classloader of its concrete class. This test
 * defines a copy of ImmutableImageLoader in a custom classloader whose getResourceAsStream
 * wraps every resource stream in a tracking stream, so getClass().getResourceAsStream(...)
 * inside fromResource routes through it and we can deterministically assert the stream the
 * method opened gets closed.
 */
class FromResourceClosesStreamTest : FunSpec({

   test("fromResource closes the resource input stream it opens") {
      val tracker = TrackingClassLoader(ImmutableImageLoader::class.java.classLoader)

      // Load the copied loader through the tracking classloader so its getClass() resolves
      // resources via tracker.getResourceAsStream.
      val klass = tracker.loadClass(ImmutableImageLoader::class.java.name)
      val loader = klass.getDeclaredConstructor().newInstance()

      // The redefined loader is a distinct type (loaded by the child classloader), so invoke
      // fromResource reflectively. The returned image is still a real ImmutableImage, because
      // the loader's own dependencies are resolved by the parent classloader.
      val image = klass.getMethod("fromResource", String::class.java)
         .invoke(loader, "/bird_small.png") as ImmutableImage

      (image.width > 0) shouldBe true
      tracker.closedCount shouldBe 1
   }
})

/**
 * Classloader that defines a private copy of [ImmutableImageLoader] (delegating everything
 * else to its parent) and wraps each resource stream it serves so that close() can be
 * observed.
 */
private class TrackingClassLoader(parent: ClassLoader) : ClassLoader(parent) {

   @Volatile
   var closedCount = 0

   private val target = ImmutableImageLoader::class.java.name

   override fun loadClass(name: String, resolve: Boolean): Class<*> {
      if (name == target) {
         findLoadedClass(name)?.let { return it }
         val path = name.replace('.', '/') + ".class"
         val bytes = parent.getResourceAsStream(path)!!.use { it.readBytes() }
         val klass = defineClass(name, bytes, 0, bytes.size)
         if (resolve) resolveClass(klass)
         return klass
      }
      return super.loadClass(name, resolve)
   }

   override fun getResourceAsStream(name: String): InputStream? {
      val delegate = parent.getResourceAsStream(name) ?: return null
      return object : FilterInputStream(delegate) {
         override fun close() {
            closedCount++
            super.close()
         }
      }
   }
}
