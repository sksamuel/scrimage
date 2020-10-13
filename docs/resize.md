Resize
=======

Resizes the canvas to the given dimensions.

This does not [scale](scale.md) the image but simply changes the dimensions of the canvas on which the image is sitting.

Specifying a larger size will pad the image with a background color and specifying a smaller size will crop the image.
This is the operation most people want when they think of crop.

When resizing, we can specify a factor to multiply the current dimensions by, such as `image.resize(0.5)`,
or we can specify target sizes specifically, such as `image.resizeTo(400,300)`.



      ImmutableImage.loader().fromResource("/before.jpg")
         .resizeTo(500, 300)
         .forWriter(JpegWriter.Default).write(".jpg");


      ImmutableImage.loader().fromResource("/before.jpg")
         .resizeTo(700, 400, Position.TopRight, Color.DARK_GRAY)
         .forWriter(JpegWriter.Default).write("resize_larger.jpg");
