package com.sksamuel.scrimage.metadata;

import java.util.Arrays;
import java.util.Objects;

public class Directory {

    private final String name;

    public String getName() {
        return name;
    }

    public Tag[] getTags() {
        return tags;
    }

    private final Tag[] tags;

    public Directory(String name, Tag[] tags) {
        this.name = name;
        this.tags = tags;
    }

    // name can be null in practice — it comes from drewmetadata's
    // `directory.getName()`, which is non-null for the bundled directory
    // implementations but the constructor doesn't enforce that. Use
    // Objects.equals / Objects.hashCode rather than the direct calls so
    // we don't throw NullPointerException from equals / hashCode.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Directory directory = (Directory) o;

        if (!Objects.equals(name, directory.name)) return false;
        return Arrays.equals(tags, directory.tags);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Arrays.hashCode(tags);
        return result;
    }
}
