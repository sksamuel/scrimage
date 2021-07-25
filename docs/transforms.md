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
