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

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.metadata.ImageMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.OutputStream;

public class PngWriter implements ImageWriter {

    public static final PngWriter MaxCompression = new PngWriter(9);
    public static final PngWriter MinCompression = new PngWriter(1);
    public static final PngWriter NoCompression = new PngWriter(0);

    private final int compressionLevel;

    public PngWriter() {
        this.compressionLevel = 9;
    }

    public PngWriter(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    // require(compressionLevel >= 0 && compressionLevel < 10, "Compression level must be between 0 (none) and 9 (max)")

    public PngWriter withMaxCompression() {
        return MaxCompression;
    }

    public PngWriter withMinCompression() {
        return MinCompression;
    }

    public PngWriter withCompression(int compression) {
        return new PngWriter(compression);
    }

    @Override
    public void write(AwtImage image, ImageMetadata metadata, OutputStream out) throws IOException {

        if (image.awt().getType() == BufferedImage.TYPE_INT_ARGB) {

            ImageInfo imi = new ImageInfo(image.width, image.height, 8, true);

            ar.com.hjg.pngj.PngWriter writer = new ar.com.hjg.pngj.PngWriter(out, imi);
            writer.setCompLevel(compressionLevel);
            writer.setFilterType(FilterType.FILTER_DEFAULT);

            DataBufferInt db = (DataBufferInt) image.awt().getRaster().getDataBuffer();
            if (db.getNumBanks() != 1) throw new RuntimeException("This method expects one bank");

            SinglePixelPackedSampleModel samplemodel = (SinglePixelPackedSampleModel) image.awt().getSampleModel();
            ImageLineInt line = new ImageLineInt(imi);
            int[] dbbuf = db.getData();

            for (int row = 0; row < imi.rows; row++) {
                int elem = samplemodel.getOffset(0, row);
                int j = 0;
                for (int col = 0; col < imi.cols; col++) {
                    int sample = dbbuf[elem];
                    elem = elem + 1;
                    line.getScanline()[j] = (sample & 0xFF0000) >> 16; // R
                    j = j + 1;
                    line.getScanline()[j] = (sample & 0xFF00) >> 8; // G
                    j = j + 1;
                    line.getScanline()[j] = sample & 0xFF; // B
                    j = j + 1;
                    line.getScanline()[j] = ((sample & 0xFF000000) >> 24) & 0xFF; // A
                    j = j + 1;
                }
                writer.writeRow(line, row);
            }
            writer.end();// end calls close

        } else {
            ImageIO.write(image.awt(), "png", out);
        }
    }
}
