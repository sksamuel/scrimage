package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.ImageParseException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Utilites for reading of an Image to an array of bytes in a specified format.
 */
public class ImageReader {

    private static Collection<Reader> readers = Arrays.asList(new JavaImageIOReader(), new PngReader(), new JavaImageIO2Reader());

    public static Image fromFile(File file) throws IOException {
        return fromFile(file, Image.CANONICAL_DATA_TYPE());
    }

    public static Image fromFile(File file, int type) throws IOException {
        return fromPath(file.toPath(), type);
    }

    public static Image fromPath(Path path) throws IOException {
        return fromPath(path, Image.CANONICAL_DATA_TYPE());
    }

    public static Image fromPath(Path path, int type) throws IOException {
        try (InputStream stream = Files.newInputStream(path)) {
            return fromStream(stream, type);
        }
    }

    public static Image fromStream(InputStream in) throws IOException {
        return fromStream(in, Image.CANONICAL_DATA_TYPE());
    }

    public static Image fromStream(InputStream in, int type) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return fromBytes(bytes, type);
    }

    public static Image fromBytes(byte[] bytes, int type) {
        Optional<Image> image = Optional.empty();
        for (Reader reader : readers) {
            if (!image.isPresent()) {
                try {
                    image = Optional.ofNullable(reader.fromBytes(bytes, type));
                } catch (Exception e) {
                }
            }
        }
        return image.orElseThrow(ImageParseException::new);
    }
}

interface Reader {

    default Image fromBytes(byte[] bytes) throws IOException {
        return fromBytes(bytes, Image.CANONICAL_DATA_TYPE());
    }

    Image fromBytes(byte[] bytes, int type) throws IOException;
}