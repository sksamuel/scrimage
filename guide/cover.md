## Cover

The cover operation will create an image that is exactly the specified dimensions, and the given image is scaled to fit, such that there is no "background". If the target dimensions do not have the same aspect ratio as the source image then some of the image will be lost as it will have to be "over scaled" to completely cover the target image.

Compare this operation with fit.

To use, invoke with the target dimenions and optionally, a scale method (defaults to Bicubic), and the position of the source image in the target (defaults to Center). That last parameter has no effect if the aspect ratios are the same.

Eg, to create a new image of 600 x 400 using defaults:
```
image.cover(600,400)
```
or to create 240x160 using fast scale:
```
image.cover(240,160, ScaleMethod.FastScale)
```

As always a picture is worth a thousand statements. In this example, the original bird image was invoked with
`bird.cover(300,150)`. As you can see, the output image is of course 300x150, but because the original image had a different aspect ratio, there is no way to exactly scale the image. So some of the top and bottom are lost in order to avoid having background showing. This operation is great when you want images of a uniform size, but you don't any background. Think like facebook cover pictures where the cover image loses a bit of the original but fits in the space perfectly.

<table>
<tr>
<th>
    Before
</th>
<th>
    After
</th>
</tr>
<tr>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_small.png"/>
</th>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_covered.png"/>
</th>
</tr>
</table>