Transforms
=========

A transform modifies an image and returns a new image derived from that input. A transform is similar to a filter, but
where a filter modifies the pixels of an image, a transform can return an entirely new image.

The code required to perform a transform is simple:

=== "Java"

    ```
    ImmutableImage transformed = image1.transform(new MyTransform())
    ```

=== "Kotlin"

    ```
    val transformed = image1.transform(MyTransform())
    ```

=== "Scala"

    ```
    val transformed = image1.transform(new MyTransform())
    ```


Click on an example to see it full screen.

| Transform | Input | Result |
| ------ | --------- | --------- |
| Background Gradient | <a href='https://raw.github.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/docs/images/background_gradient_input1.jpeg' width='200'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png'><img src='https://raw.github.com/sksamuel/scrimage/master/docs/images/background_gradient_output1.png' width='200'><a/> |
