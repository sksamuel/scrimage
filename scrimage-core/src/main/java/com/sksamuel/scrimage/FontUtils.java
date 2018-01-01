package com.sksamuel.scrimage;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

public class FontUtils {

    public Rectangle2D fontBounds(String text, Font font) {
        return font.getStringBounds(text, new FontRenderContext(font.getTransform(), false, false)).getBounds2D();
    }
    public static Font createFont(String name, int size) throws IOException, FontFormatException {
        return new Font(name, Font.PLAIN, size);
    }

    public static Font createTrueType(InputStream in, int size) throws IOException, FontFormatException {
        assert (in != null);
        Font font = Font.createFont(Font.TRUETYPE_FONT, in);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(Font.PLAIN, size);
    }

    public static String[] fontNames() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    public static Font plain(String name, int size) {
        return new Font(name, Font.PLAIN, size);
    }

    public static Font italic(String name, int size) {
        return new Font(name, Font.ITALIC, size);
    }

    public static Font bold(String name, int size) {
        return new Font(name, Font.BOLD, size);
    }

    public static Font boldItalic(String name, int size) {
        return new Font(name, Font.BOLD | Font.ITALIC, size);
    }

}
