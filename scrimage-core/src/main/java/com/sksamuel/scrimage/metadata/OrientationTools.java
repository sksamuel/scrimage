package com.sksamuel.scrimage.metadata;

import com.sksamuel.scrimage.ImmutableImage;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrientationTools {

   // ExifIFD0Directory.getName() always returns this literal constant. Hold it as a
   // static final value rather than allocating a new ExifIFD0Directory (and its
   // descriptor) on every imageOrientationsOf call just to read the name.
   private static final String EXIF_IFD0_DIR_NAME = "Exif IFD0";

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

      Tag[] tags = Arrays.stream(metadata.getDirectories())
         .filter(dir -> dir.getName().equals(EXIF_IFD0_DIR_NAME))
         .findFirst()
         .map(Directory::getTags)
         .orElseGet(() -> new Tag[0]);

      // Use LinkedHashSet (encounter/tag-declaration order) rather than the
      // unordered HashSet from Collectors.toSet(). When more than one tag with
      // id 274 is present, the caller picks the first element via findFirst();
      // with a HashSet that choice depended on hash iteration order, so the same
      // image could be reoriented differently across runs/JVMs.
      Set<Tag> tag274s = Arrays.stream(tags)
         .filter(t -> t.getType() == 274)
         .collect(Collectors.toCollection(LinkedHashSet::new));

      if (tag274s.size() == 1) {
         return tag274s.stream()
            .map(OrientationTools::fromTag)
            .collect(Collectors.toCollection(LinkedHashSet::new));
      } else {
         return tag274s.stream()
            .filter(t -> t.getName().toLowerCase().equals("orientation"))
            .map(OrientationTools::fromTag)
            .collect(Collectors.toCollection(LinkedHashSet::new));
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
