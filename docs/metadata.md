Metadata
=========

Scrimage builds on the [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) project to provide the ability to read metadata.

This can be done in two ways. Firstly, the metadata is attached to the image if it was available when you loaded the image
from the a stream, file or resource. Then you can call `image.metadata` to get a handle to the metadata object.

Secondly, the metadata can be loaded without needing to create an image instance, by using the methods on `ImageMetadata`.

Once you have the metadata object, you can invoke `directories` or `tags` to see the information.

For example, we can print out all the tags in an image via this code:


=== "Java"

    ```
    ImageMetadata meta = ImageMetadata.fromStream(stream);
    Arrays.stream(meta.tags()).forEach(tag -> System.out.println(tag));
    ```

=== "Kotlin"

    ```
    val meta = ImageMetadata.fromStream(stream)
    meta.tags().forEach {
      println(it)
    }
    ```

=== "Scala"

    ```
    val meta = ImageMetadata.fromStream(stream)
    meta.tags().asScala.foreach { tag =>
      println(tag)
    }
    ```

And the output will look something like:

```
...
Tag{name='Compression Type', type=-3, rawValue='0', value='Baseline'}
Tag{name='Data Precision', type=0, rawValue='8', value='8 bits'}
Tag{name='Image Height', type=1, rawValue='405', value='405 pixels'}
Tag{name='Image Width', type=3, rawValue='594', value='594 pixels'}
Tag{name='Resolution Units', type=7, rawValue='1', value='inch'}
Tag{name='X Resolution', type=8, rawValue='300', value='300 dots'}
Tag{name='Y Resolution', type=10, rawValue='300', value='300 dots'}
Tag{name='Thumbnail Width Pixels', type=12, rawValue='0', value='0'}
Tag{name='Thumbnail Height Pixels', type=13, rawValue='0', value='0'}
Tag{name='Image Width', type=256, rawValue='4928', value='4928 pixels'}
Tag{name='Image Height', type=257, rawValue='3280', value='3280 pixels'}
Tag{name='Bits Per Sample', type=258, rawValue='8 8 8', value='8 8 8 bits/component/pixel'}
Tag{name='Photometric Interpretation', type=262, rawValue='2', value='RGB'}
Tag{name='Image Description', type=270, rawValue='during the Sky Bet Championship match between Middlesbrough and Wolverhampton Wanderers at Riverside Stadium on April 14, 2015 in Middlesbrough, England.', value='during the Sky Bet Championship match between Middlesbrough and Wolverhampton Wanderers at Riverside Stadium on April 14, 2015 in Middlesbrough, England.'}
Tag{name='Make', type=271, rawValue='NIKON CORPORATION', value='NIKON CORPORATION'}
Tag{name='Model', type=272, rawValue='NIKON D4S', value='NIKON D4S'}
Tag{name='Orientation', type=274, rawValue='1', value='Top, left side (Horizontal / normal)'}
Tag{name='Samples Per Pixel', type=277, rawValue='3', value='3 samples/pixel'}
Tag{name='X Resolution', type=282, rawValue='72', value='72 dots per inch'}
Tag{name='Y Resolution', type=283, rawValue='72', value='72 dots per inch'}
...
```
