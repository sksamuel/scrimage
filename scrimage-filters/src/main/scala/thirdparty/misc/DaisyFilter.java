package thirdparty.misc;

/**
 * @author Stephen Samuel
 */

import thirdparty.romainguy.BlendComposite;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;

/**
 * @author Sarah Schölzel
 */
public class DaisyFilter {

    private int[] pixels;

    public BufferedImage filter(BufferedImage bi) {
        if (bi.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
            bi = convertType(bi, BufferedImage.TYPE_INT_ARGB);
        }

        BufferedImage dstImg = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        //Grayscale, Contrast
        bi = getGrayScale(bi);
        bi = applyBrightnessAndContrast(bi, 0, 15);

        //Copy Image: more red,more yellow(= less blue)
        initPixelsArray(bi);
        int[] changedPixels = changeColor();
        BufferedImage changedColor = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        changedColor.setRGB(0, 0, bi.getWidth(), bi.getHeight(), changedPixels, 0, bi.getWidth());

        //Blending Mode: Soft Light, 50%
        Graphics2D gdst = dstImg.createGraphics();
        gdst.drawImage(bi, 0, 0, null);
        gdst.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.SCREEN, 0.6f));
        gdst.drawImage(changedColor, 0, 0, bi.getWidth(), bi.getHeight(), null);

        return dstImg;
    }

    private BufferedImage applyBrightnessAndContrast(BufferedImage bi, double brightness, double contrast) {

        final double gamma = 0.25;

        if (contrast > 0) {
            contrast = (100 * Math.pow(contrast - 1, 1 / gamma) / Math.pow(100, 1 / gamma)) + 1;
        } else if (contrast == 0) {
            contrast = 1;
        } else {
            contrast = 1 / ((100 * Math.pow(-contrast + 1, 1 / gamma) / Math.pow(100, 1 / gamma)) + 1);
        }

        if (brightness > 0) {
            brightness = (100 * Math.pow(brightness, 1 / gamma) / Math.pow(100, 1 / gamma)) + 1;
        } else if (brightness == 0) {
            brightness = 0;
        } else {
            brightness = 1 / ((100 * Math.pow(-brightness, 1 / gamma) / Math.pow(100, 1 / gamma)) + 1);
        }

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final BufferedImage out = new BufferedImage(w, h, bi.getType());

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = bi.getRGB(x, y);

                // get the rgb-values
                int alpha = (int) ((rgb & 0xff000000l) >> 24);
                int r = ((rgb & 0x00ff0000) >> 16);
                int g = ((rgb & 0x0000ff00) >> 8);
                int b = ((rgb & 0x000000ff));

                // apply brightness filter
//
//                r = (int) (r * brightness);
//                g = (int) (g * brightness);
//                b = (int) (b * brightness);

                // convert to YCbCr
                double Y = r * 0.299 + g * 0.587 + b * 0.114;
                double Cb = r * -0.168736 + g * -0.331264 + b * 0.5;
                double Cr = r * 0.5 + g * -0.418688 + b * -0.081312;

                // apply contrast filter
                Y = (Y + brightness - 127) * contrast + 127;
                Cb = Cb * contrast;
                Cr = Cr * contrast;

                // convert back to RGB
                r = (int) (Y + (Cr * 1.402));
                g = (int) (Y + (Cb * -0.344136) + (Cr * -0.714136));
                b = (int) (Y + (Cb * 1.772));

                // check sizes of return values
                if (alpha > 255) {
                    alpha = 255;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                if (g > 255) {
                    g = 255;
                } else if (g < 0) {
                    g = 0;
                }
                if (r > 255) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }
                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                rgb = ((alpha & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                out.setRGB(x, y, rgb);
            }
        }

        return out;
    }

    private BufferedImage getGrayScale(BufferedImage bi) {
        // the following code is by David Ekholm from the GrayscaleFilter
        final Graphics2D g = bi.createGraphics();
        final RenderingHints rhs = g.getRenderingHints();

        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ColorConvertOp theOp = new ColorConvertOp(cs, rhs);

        final BufferedImage dstImg = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        theOp.filter(bi, dstImg);

        return dstImg;
    }

    private BufferedImage convertType(BufferedImage src, int type) {
        ColorConvertOp cco = new ColorConvertOp(null);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), type);
        cco.filter(src, dest);
        return dest;
    }

    /**
     * takes the pixels from a BufferedImage and stores them in an array
     */
    private void initPixelsArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

    }

    /**
     * changes the color of the image - more red and less blue
     *
     * @return new pixel array
     */
    private int[] changeColor() {
        int[] changedPixels = new int[pixels.length];
        double frequenz = 2 * Math.PI / 1020;

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (argb >> 24) & 0xff;
            int r = (argb >> 16) & 0xff;
            int g = (argb >> 8) & 0xff;
            int b = argb & 0xff;

            r = (int) (255 * Math.sin(frequenz * r));
            b = (int) (-255 * Math.cos(frequenz * b) + 255);

            changedPixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return changedPixels;
    }
}
