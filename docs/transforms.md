Transforms
=========

A transform modifies an image and returns a new image derived from that input. A transform is similar to a filter, but
where a filter modifies the pixels of an image, a transform can return an entirely new image.

Click on an example to see it full screen.

### Background Gradient

Produces a linear gradient background underlay for the two most dominent colours of an image.

=== "Java"

    ```
    ImmutableImage transformed = image1.transform(new BackgroundGradient(400, 300))
    ```

=== "Kotlin"

    ```
    val transformed = image1.transform(BackgroundGradient(400, 300))
    ```

=== "Scala"

    ```
    val transformed = image1.transform(new BackgroundGradient(400, 300))
    ```

|  Input | Output |
| ------ | --------- |
| <a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg' width='300'><a/> |<a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png' width='300'><a/> |


### Dominant Gradient

Produces a linear gradient copy of the image for the two most dominent colours of an image.

=== "Java"

    ```
    ImmutableImage transformed = image1.transform(new DominantGradient())
    ```

=== "Kotlin"

    ```
    val transformed = image1.transform(DominantGradient())
    ```

=== "Scala"

    ```
    val transformed = image1.transform(new DominantGradient())
    ```

|  Input | Output |
| ------ | --------- |
| <a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg' width='300'><a/> |<a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png' width='300'><a/> |


### Tile

Tiles the source image into a larger image. `TileFilter(columns, rows)` produces an image `columns` times the source width and `rows` times the source height, with the source drawn into every cell — so `TileFilter(3, 2)` repeats the image 3 across and 2 down.

=== "Java"

    ```
    ImmutableImage transformed = image1.transform(new TileFilter(3, 2))
    ```

=== "Kotlin"

    ```
    val transformed = image1.transform(TileFilter(3, 2))
    ```

=== "Scala"

    ```
    val transformed = image1.transform(new TileFilter(3, 2))
    ```

|  Input | Output |
| ------ | --------- |
| <a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/tile_input1.jpeg'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/tile_input1.jpeg' width='300'><a/> |<a href='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/tile_output1.png'><img src='https://raw.githubusercontent.com/sksamuel/scrimage/master/docs/images/tile_output1.png' width='300'><a/> |
