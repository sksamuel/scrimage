Introduction
============

The core data type in Scrimage is the `ImmutableImage` class.
This wraps a Java AWT image with functions that return copies of the image instead of mutating it.

We can [create](create.md) such an instance specifying the dimensions, or we can [load](io.md) an image from one of the supported formats.

Once we have an image in memory, we can perform an operation on it that returns a new image. Such as [scale](scale.md), [resize](resize.md),
[brightness](brightness.md), [autocrop](autocrop.md) and so on.

There are [pixel functions](pixels.md) available, ways to interact with different [color namespaces](colors.md),
dozens of [filters](filters.md), or [composites](composites.md).

If the underlying source supports it, we can interrogate the associated [metadata](metadata.md).

Finally, we can [save](io.md) the image back to a file, byte array or stream.
