package com.sksamuel.scrimage;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.sksamuel.scrimage.metadata.ImageMetadata;
import com.sksamuel.scrimage.metadata.Tag;

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

    // The version of this method in scrimage fails when images have tags of type 274
    // that aren't actually orientation tags, which happens occasionally.
    // It seems to happen particularly with Sony cameras
    public static ImmutableImage reorient(ImmutableImage image, ImageMetadata metadata) {


        Set<String> imageOrientations = imageOrientationsOf(metadata);
        String first = imageOrientations.stream().findFirst().orElse("-1");
        switch (first) {
            // normal
            case "1":
                return image;
            // Flip horizontally
            case "2":
                return image.flipX();
            // Rotate 180 degrees
            case "3":
                return image.rotateLeft().rotateLeft();
            // Rotate 180 degrees and flip horizontally
            case "4":
                return image.rotateLeft().rotateLeft().flipX();
            // Rotate 90 degrees clockwise and flip horizontally
            case "5":
                return image.rotateRight().flipX();
            // Rotate 90 degrees clockwise
            case "6":
                return image.rotateRight();
            // Rotate 90 degrees anti-clockwise and flip horizontally
            case "7":
                return image.rotateLeft().flipX();
            // Rotate 90 degrees anti-clockwise
            case "8":
                return image.rotateLeft();
            // Unknown, keep the orginal image
            default:
                return image;
        }
    }

    // returns the values of the orientation tag
    // Sometimes (with sony cameras) there are multiple tags with id 274 so we must also
    // check the name, if there is more than one.
    private static Set<String> imageOrientationsOf(ImageMetadata metadata) {

        String exifIFD0DirName = new ExifIFD0Directory().getName();

        Tag[] tags = Arrays.stream(metadata.getDirectories())
                .filter(dir -> dir.getName().equals(exifIFD0DirName))
                .findFirst()
                .map(Directory::getTags)
                .orElseGet(() -> new Tag[0]);

        Set<Tag> tag274s = Arrays.stream(tags).filter(t -> t.getType() == 274).collect(Collectors.toSet());

        if (tag274s.size() == 1) {
            return tag274s.stream()
                    .map(Tag::getRawValue)
                    .collect(Collectors.toSet());
        } else {
            return tag274s.stream()
                    .filter(t -> t.getName().toLowerCase().equals("orientation"))
                    .map(Tag::getRawValue)
                    .collect(Collectors.toSet());
        }
    }
}
