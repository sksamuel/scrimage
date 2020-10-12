Metadata
=========

Scrimage builds on the [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) project to provide the ability to read metadata.

This can be done in two ways. Firstly, the metadata is attached to the image if it was available when you loaded the image
from the `Image.fromStream`, `Image.fromResource`, or `Image.fromFile` methods. Then you can call `image.metadata` to get
a handle to the metadata object.

Secondly, the metadata can be loaded without an Image being needed, by using the methods on ImageMetadata.

Once you have the metadata object, you can invoke `directories` or `tags` to see the information.


```

