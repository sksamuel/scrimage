package com.sksamuel.scrimage;

import com.drew.metadata.exif.ExifIFD0Directory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Orientation {

  public static Boolean requiresReorientation(ImageMetadata metadata) {

    Set<String> imageOrientations = imageOrientationsOf(metadata);
    String first = imageOrientations.stream().findFirst().orElse("-1");
    switch (first) {
      case "2":
      case "3":
      case "4":
      case "5":
      case "6":
      case "7":
      case "8":
        return true;
      default:
        return false;
    }
  }

  public static Image reorient(Image image, ImageMetadata metadata) {

    Set<String> imageOrientations = imageOrientationsOf(metadata);
    String first = imageOrientations.stream().findFirst().orElse("-1");
    switch (first) {
      // normal
      case "1":
        return image;
      // Flip horizontally
      case "2":
        // Rotate 180 degrees
        image.flipX();
      case "3":
        // Rotate 180 degrees and flip horizontally
        image.rotateLeft().rotateLeft();
      case "4":
        // Rotate 90 degrees clockwise and flip horizontally
        image.rotateLeft().rotateLeft().flipX();
      case "5":
        // Rotate 90 degrees clockwise
        image.rotateRight().flipX();
      case "6":
        // Rotate 90 degrees anti-clockwise and flip horizontally
        image.rotateRight();
      case "7":
        // Rotate 90 degrees anti-clockwise
        return image.rotateLeft().flipX();
      case "8":
        return image.rotateLeft();
      // Unknown, keep the orginal image
      default:
        return image;
    }
  }

  // returns the values of the orientation tag
  private static Set<String> imageOrientationsOf(ImageMetadata metadata) {

    String exifIFD0DirName = new ExifIFD0Directory().getName();

    Tag[] tags = Arrays.stream(metadata.directories())
            .filter(dir -> dir.name().equals(exifIFD0DirName))
            .findFirst()
            .map(Directory::tags)
            .orElseGet(() -> new Tag[0]);

    return Arrays.stream(tags)
            .filter(tag -> tag.type() == 274)
            .map(Tag::rawValue)
            .collect(Collectors.toSet());
  }
}
