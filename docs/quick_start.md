### Including Scrimage in your project

Scrimage is available on maven central. There are several modules.

One is the `scrimage-core` library which is required. The others are `scrimage-filters`, `scrimage-formats-extra`, `scrimage-webp`.

They are split into several modules because the image filters is a large jar, and most people just want the basic resize/scale/load/save functionality.

The`scrimage-formats-extra` package brings in readers/writers for less common formats such as BMP, Tiff or PCX. These formats are wrappers
around the [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys) library.


=== "Gradle"

    ```groovy
    implementation("com.sksamuel.scrimage:scrimage-core:<version>")
    ```

=== "SBT"

    ```scala
    libraryDependencies += "com.sksamuel.scrimage" % "scrimage-core" % "$version"
    ```

=== "Maven"

    ```xml
    <dependency>
        <groupId>com.sksamuel.scrimage</groupId>
        <artifactId>scrimage-core</artifactId>
        <version>${version}</version>
    </dependency>
    ```
