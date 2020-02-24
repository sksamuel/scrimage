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
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ImageWriter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
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
    public void write(AwtImage image, OutputStream out) throws IOException {

        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageWriteParam params = writer.getDefaultWriteParam();

        if (progressive) {
            params.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
        } else {
            params.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
        }

        try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(out)) {
            writer.setOutput(output);
            writer.write(null, new IIOImage(image.awt(), null, null), params);
            writer.dispose();
        }

//        IOUtils.closeQuietly(out);
    }
}
