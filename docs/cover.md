Cover
======

The cover operation creates an image with the specified dimensions, with the original image
scaled to cover the whole canvas, such that there is no added "background".

If the source and target dimensions do not have the same aspect ratio then part of the source
image will be lost as it will have to be "over scaled" to completely cover the target image.

This is similar to taking a 16:9 movie and resizing it for a 4:3 screen. You can either lose part
of the image (this operation) or resize it so there is empty space on two sides (the fit operation).

Compare this operation with [fit](fit.md).

To use, invoke with the target dimensions and optionally,
a scale method (defaults to ScaleMethod.Bicubic), and the position of the source
 image in the target (defaults to Position.Center).

 That last parameter has no effect if the aspect ratios are the same,
 since the image will cover the target without any loss.

### Examples

Using this image scaled to 640 x 360 as our input:

![source image](images/input_640_360.jpg)

| Code | Output |
| ---- | ------ |
| `image.cover(400, 300)`                       | <img src="/images/cover_400_300.jpg"/> |
| `image.cover(500, 200)`                       | <img src="/images/cover_500_200.jpg"/> |
| `image.cover(500, 200, Position.TopLeft)`     | <img src="/images/cover_500_200_top_left.jpg"/> |
| `image.cover(400, 400)`                       | <img src="/images/cover_400_400.jpg"/> |
| `image.cover(400, 400, Position.CenterRight)` | <img src="/images/cover_400_400_center_right.jpg"/> |

