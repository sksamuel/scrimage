package com.sksamuel.scrimage.metadata;

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

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Tag tag = (Tag) o;

      if (type != tag.type) return false;
      if (!name.equals(tag.name)) return false;
      if (!rawValue.equals(tag.rawValue)) return false;
      return value.equals(tag.value);
   }

   @Override
   public int hashCode() {
      int result = name.hashCode();
      result = 31 * result + type;
      result = 31 * result + rawValue.hashCode();
      result = 31 * result + value.hashCode();
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
