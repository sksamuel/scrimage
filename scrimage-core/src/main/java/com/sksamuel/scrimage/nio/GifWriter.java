/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;

public class GifWriter implements ImageWriter {

    public static GifWriter Progressive = new GifWriter(true);
    public static GifWriter Default = new GifWriter(false);

    private final Boolean progressive;

    public GifWriter(Boolean progressive) {
        this.progressive = progressive;
    }

    public GifWriter withProgressive(Boolean progressive) {
        return new GifWriter(progressive);
    }

    @Override
    public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {

        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        // dispose() in a finally that spans every use of the writer. Acquiring it
        // here but only disposing inside the write try-block leaked the native
        // GIF writer if anything in between threw — e.g. getDefaultWriteParam()
        // or setProgressiveMode() raising a RuntimeException from a writer plugin.
        try {
            ImageWriteParam params = writer.getDefaultWriteParam();

            if (params.canWriteProgressive()) {
                if (progressive) {
                    params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
                } else {
                    params.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
                }
            }

            try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out)) {
                writer.setOutput(output);
                writer.write(null, new IIOImage(detached(image.awt()), null, null), params);
            }
        } finally {
            writer.dispose();
        }
    }

    /**
     * The JDK GIF writer's write() path ignores a raster's translation and reads single band
     * byte rasters (TYPE_BYTE_INDEXED, TYPE_BYTE_GRAY, 8-bit palettes) from the parent's origin,
     * so a sub-raster view, such as one from BufferedImage.getSubimage, was encoded with the
     * wrong pixels. Copy such views into a fresh image with the same colour model first.
     */
    private static BufferedImage detached(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        if (raster.getParent() == null && raster.getSampleModelTranslateX() == 0 && raster.getSampleModelTranslateY() == 0)
            return image;
        ColorModel cm = image.getColorModel();
        WritableRaster copy = cm.createCompatibleWritableRaster(image.getWidth(), image.getHeight());
        copy.setRect(-raster.getMinX(), -raster.getMinY(), raster);
        return new BufferedImage(cm, copy, cm.isAlphaPremultiplied(), null);
    }
}
