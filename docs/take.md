Take
====

Returns a new Image which is the source image, but only keeping a specified number of pixels from the top, bottom, left or right.

For example, `takeLeft(k)` will return a subimage with bounds `[0, 0, k, height]`.


### Examples

Using this image scaled to 640 x 360 as our input:

![source image](images/input_640_360.jpg)

| Code | Output |
| ---- | ------ |
| `image.takeLeft(300)`                    | <img src="/images/take_l300.jpg"/> |
| `image.takeLeft(300).takeTop(200)`       | <img src="/images/take_l300_t200.jpg"/> |
| `image.takeRight(400).takeBottom(200)`   | <img src="/images/take_r400_b200.jpg"/> |
