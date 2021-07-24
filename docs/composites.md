Composites
=========


Scrimage comes with several composites. A composite merges two images with each pixel composited together using the rule provided
by the composite class.

This grid shows the effect of compositing a palm tree image over a US mailbox image. The first column is the composite
with a value of 0.5f, and the second column with 1f. Note, if you reverse the order of the images then the effects would
be reversed.

The code required to perform a composite is simple:

=== "Java"

    ```
    ImmutableImage composed = image1.composite(new XYZComposite(alpha), image2)
    ```

=== "Kotlin"

    ```
    val composed = image1.composite(XYZComposite(alpha), image2)
    ```

=== "Scala"

    ```
    val composed = image1.composite(new XYZComposite(alpha), image2)
    ```


Click on an example to see it full screen.

| Composite | Alpha 0.5f | Alpha 1f |
| ------ | --------- | --------- |
| average | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_1.0_small.png'><a/> |
| blue | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_1.0_small.png'><a/> |
| color | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_1.0_small.png'><a/> |
| colorburn | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_1.0_small.png'><a/> |
| colordodge | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_1.0_small.png'><a/> |
| diff | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_1.0_small.png'><a/> |
| green | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_1.0_small.png'><a/> |
| grow | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_1.0_small.png'><a/> |
| hue | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_1.0_small.png'><a/> |
| hard | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_1.0_small.png'><a/> |
| heat | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_1.0_small.png'><a/> |
| lighten | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_1.0_small.png'><a/> |
| negation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_small.png'><a/> |
| luminosity | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_1.0_small.png'><a/> |
| multiply | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_1.0_small.png'><a/> |
| negation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_small.png'><a/> |
| normal | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_1.0_small.png'><a/> |
| overlay | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_1.0_small.png'><a/> |
| red | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_1.0_small.png'><a/> |
| reflect | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_1.0_small.png'><a/> |
| saturation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_1.0_small.png'><a/> |
| screen | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_1.0_small.png'><a/> |
| subtract | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_1.0_small.png'><a/> |
