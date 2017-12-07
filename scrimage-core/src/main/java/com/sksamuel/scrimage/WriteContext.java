package com.sksamuel.scrimage;

import com.sksamuel.scrimage.nio.ImageWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteContext {

    private final ImageWriter writer;
    private final Image image;

    public WriteContext(ImageWriter writer, Image image) {
        this.writer = writer;
        this.image = image;
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer.write(image, bos);
        return bos.toByteArray();
    }

    public ByteArrayInputStream stream() throws IOException {
        return new ByteArrayInputStream(bytes());
    }

    public Path write(String path) throws IOException {
        return write(Paths.get(path));
    }

    public File write(File file) throws IOException {
        write(file.toPath());
        return file;
    }

    public Path write(Path path) throws IOException {
        try (OutputStream out = Files.newOutputStream(path)) {
            writer.write(image, out);
        }
        return path;
    }

    public void write(OutputStream out) throws IOException {
        writer.write(image, out);
    }
}
