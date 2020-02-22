package com.sksamuel.scrimage;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.sksamuel.scrimage.nio.PngWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ImageMetadata {

  public static ImageMetadata empty = new ImageMetadata(new Directory[0]);

  public static ImageMetadata fromImage(ImmutableImage image) throws IOException, ImageProcessingException {
    try (ByteArrayInputStream stream = new ByteArrayInputStream(image.bytes(PngWriter.NoCompression))) {
      Metadata metadata = ImageMetadataReader.readMetadata(stream);
      return fromMetadata(metadata);
    }
  }

  public static ImageMetadata fromPath(Path path) throws IOException, ImageProcessingException {
    return fromFile(path.toFile());
  }

  public static ImageMetadata fromFile(File file) throws IOException, ImageProcessingException {
    try (InputStream in = Files.newInputStream(file.toPath())) {
      return fromStream(in);
    }
  }

  public static ImageMetadata fromResource(String resource) throws IOException, ImageProcessingException {
    try (InputStream in = ImageMetadata.class.getResourceAsStream(resource)) {
      return fromStream(in);
    }
  }

  public static ImageMetadata fromStream(InputStream in) throws ImageProcessingException, IOException {
    return fromMetadata(ImageMetadataReader.readMetadata(in));
  }

  public static ImageMetadata fromBytes(byte[] bytes) throws IOException, ImageProcessingException {
    return fromStream(new ByteArrayInputStream(bytes));
  }

  public static ImageMetadata fromMetadata(Metadata metadata) {
    Directory[] dirs = StreamSupport.stream(metadata.getDirectories().spliterator(), false).map(dir -> {
      Tag[] tags = dir.getTags().stream().map(tag ->
              new Tag(tag.getTagName(), tag.getTagType(), dir.getString(tag.getTagType()), tag.getDescription())
      ).collect(Collectors.toList()).toArray(new Tag[0]);
      return new Directory(dir.getName(), tags);
    }).collect(Collectors.toList()).toArray(new Directory[0]);
    return new ImageMetadata(dirs);
  }

  private final Directory[] directories;

  public Directory[] getDirectories() {
    return directories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ImageMetadata that = (ImageMetadata) o;

    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(directories, that.directories);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(directories);
  }

  public ImageMetadata(Directory[] directories) {
    this.directories = directories;
  }

  public Tag[] tagsBy(Predicate<Tag> f) {
    return Arrays.stream(tags()).filter(f).toArray(Tag[]::new);
  }

  public Tag[] tags() {
    return Arrays.stream(directories).flatMap(dir -> Arrays.stream(dir.getTags())).toArray(Tag[]::new);
  }
}