package com.sksamuel.scrimage.metadata;

import java.util.Objects;

public class Tag {

   private final String name;
   private final int type;
   private final String rawValue;
   private final String value;

   public Tag(String name, int type, String rawValue, String value) {
      this.name = name;
      this.type = type;
      this.rawValue = rawValue;
      this.value = value;
   }

   // rawValue and value can both be null in practice — they come from
   // drewmetadata's `dir.getString(tagType)` and `tag.getDescription()`,
   // which return null for some real EXIF tags. Use Objects.equals /
   // Objects.hashCode rather than calling .equals / .hashCode directly
   // to avoid NullPointerException on those metadata records.

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Tag tag = (Tag) o;

      if (type != tag.type) return false;
      if (!Objects.equals(name, tag.name)) return false;
      if (!Objects.equals(rawValue, tag.rawValue)) return false;
      return Objects.equals(value, tag.value);
   }

   @Override
   public int hashCode() {
      int result = Objects.hashCode(name);
      result = 31 * result + type;
      result = 31 * result + Objects.hashCode(rawValue);
      result = 31 * result + Objects.hashCode(value);
      return result;
   }

   public String getName() {
      return name;
   }

   public int getType() {
      return type;
   }

   public String getRawValue() {
      return rawValue;
   }

   public String getValue() {
      return value;
   }

   @Override
   public String toString() {
      final StringBuffer sb = new StringBuffer("Tag{");
      sb.append("name='").append(name).append('\'');
      sb.append(", type=").append(type);
      sb.append(", rawValue='").append(rawValue).append('\'');
      sb.append(", value='").append(value).append('\'');
      sb.append('}');
      return sb.toString();
   }
}
