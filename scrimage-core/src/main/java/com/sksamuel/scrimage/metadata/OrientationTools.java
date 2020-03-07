package com.sksamuel.scrimage.metadata;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.sksamuel.scrimage.ImmutableImage;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class OrientationTools {

   public static Boolean requiresReorientation(ImageMetadata metadata) {

      Set<Orientation> imageOrientations = imageOrientationsOf(metadata);
      Orientation first = imageOrientations.stream().findFirst().orElse(Orientation.Zero);
      return first != Orientation.Zero;
   }

   // The version of this method in scrimage fails when images have tags of type 274
   // that aren't actually orientation tags, which happens occasionally.
   // It seems to happen particularly with Sony cameras
   public static ImmutableImage reorient(ImmutableImage image, ImageMetadata metadata) {
      Set<Orientation> imageOrientations = imageOrientationsOf(metadata);
      Orientation first = imageOrientations.stream().findFirst().orElse(Orientation.Zero);
      switch (first) {
         // normal
         case Zero:
            return image;
         // Flip horizontally
         case ZeroMirrored:
            return image.flipX();
         // Rotate 180 degrees
         case OneEighty:
            return image.rotateLeft().rotateLeft();
         // Rotate 180 degrees and flip horizontally
         case OneEightyMirrored:
            return image.rotateLeft().rotateLeft().flipX();
         // Rotate 90 degrees clockwise and flip horizontally
         case Ninety:
            return image.rotateRight().flipX();
         // Rotate 90 degrees clockwise
         case NinetyMirrored:
            return image.rotateRight();
         // Rotate 90 degrees anti-clockwise and flip horizontally
         case TwoSeventy:
            return image.rotateLeft().flipX();
         // Rotate 90 degrees anti-clockwise
         case TwoSeventyMirrored:
            return image.rotateLeft();
         // Unknown, keep the orginal image
         default:
            return image;
      }
   }

   // returns the values of the orientation tag
   // Sometimes (with sony cameras) there are multiple tags with id 274 so we must also
   // check the name, if there is more than one.
   static Set<Orientation> imageOrientationsOf(ImageMetadata metadata) {

      String exifIFD0DirName = new ExifIFD0Directory().getName();

      Tag[] tags = Arrays.stream(metadata.getDirectories())
         .filter(dir -> dir.getName().equals(exifIFD0DirName))
         .findFirst()
         .map(Directory::getTags)
         .orElseGet(() -> new Tag[0]);

      Set<Tag> tag274s = Arrays.stream(tags).filter(t -> t.getType() == 274).collect(Collectors.toSet());

      if (tag274s.size() == 1) {
         return tag274s.stream()
            .map(OrientationTools::fromTag)
            .collect(Collectors.toSet());
      } else {
         return tag274s.stream()
            .filter(t -> t.getName().toLowerCase().equals("orientation"))
            .map(OrientationTools::fromTag)
            .collect(Collectors.toSet());
      }
   }

   private static Orientation fromTag(Tag tag) {
      try {
         int i = Integer.parseInt(tag.getRawValue());
         return Orientation.fromRawValue(i).orElse(Orientation.Zero);
      } catch (NumberFormatException e) {
         return Orientation.Zero;
      }
   }
}
