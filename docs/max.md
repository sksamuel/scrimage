Max
====

Scales an image to be as large as possible to fit into the specified dimensions whilst maintaining
aspect ratio. This operation will **not** pad the image to ensure it matches the dimensions exactly.

For example, starting with an image that is 300x200 and invoking `max(420,300)` will result in an image that is 420x280, because
that is as large as the image can be without exceeding the required dimensions.

Similarly, starting with 600x400 and invoking `max(400,100)` will result in an image that is 150x100.

The difference between max and [fit](fit.md), is that fit will pad out the canvas to the specified dimensions,
whereas max will not. In other words, max and fit will scale the image equally, but fit will pad the canvas
with a background color so that it precisely matches the input dimensions.



### Examples

Using this image scaled to 640 x 360 as our input:

![source image](images/input_640_360.jpg)

| Code | Output |
| ---- | ------ |
| `image.max(400, 300)`       | ![image](images/max_400_300.jpg) |
| `image.max(300, 300)`       | ![image](images/max_300_300.jpg) |
| `image.max(200, 400)`       | ![image](images/max_200_400.jpg) |
