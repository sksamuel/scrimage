package com.sksamuel.scrimage.metadata;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Directory directory = (Directory) o;

        if (!name.equals(directory.name)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(tags, directory.tags);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(tags);
        return result;
    }
}
