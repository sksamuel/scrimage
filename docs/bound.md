Bound
=====

Ensures that the image is no larger than specified dimensions.

If the source image is larger, it will be scaled down, maintaining aspect ratio.

If the source image is smaller, it will be returned unmodified.

This is useful when you want to ensure images do not exceed a give size, but you
don't want to scale up the image if is already smaller.


### Examples

Using this image scaled to 640 x 360 as our input:

![source image](images/input_640_360.jpg)

| Code | Output |
| ---- | ------ |
| `image.bound(400, 300)`                  | <img src="/images/bound_400_300.jpg"/> |
| `image.bound(500, 200)`                  | <img src="/images/bound_500_200.jpg"/> |
| `image.bound(300, 500)`                  | <img src="/images/bound_300_500.jpg"/> |
