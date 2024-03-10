### Including Scrimage in your project

Scrimage is available on maven central. There are several modules.

One is the `scrimage-core` library which is required. The others are `scrimage-filters`, `scrimage-formats-extra`, `scrimage-webp`.
And if you're using Scala you can also add `scrimage-scala_2.12` or `scrimage-scala_2.13` for enhanced scala functions.

They are split into several modules because the image filters is a large jar, and most people just want the basic resize/scale/load/save functionality.

The`scrimage-formats-extra` package brings in readers/writers for less common formats such as BMP, Tiff or PCX. These formats are wrappers
around the [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys) library.


=== "Gradle"

    ```groovy
    implementation("com.sksamuel.scrimage:scrimage-core:<version>")
    ```

=== "SBT"

    ```scala
    libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-core" % "$version"
    libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-scala" % "$version"
    ```

=== "Maven"

    ```xml
    <dependency>
        <groupId>com.sksamuel.scrimage</groupId>
        <artifactId>scrimage-core</artifactId>
        <version>${version}</version>
    </dependency>
    ```




### Scala Helpers

If you are using Scala and you have added the scala module, then add the import
`import com.sksamuel.scrimage.scala._` to bring into scope some useful implicits.

Firstly, an implicit `PNGWriter` so you do not have to specify it when outputting images.
Secondly, a conversion to / from
`java.awt.Color` and Scrimage's `RGBColor`.
Lastly, forall, foreach and map methods on `ImmutableImage` which work with Scala functions.
