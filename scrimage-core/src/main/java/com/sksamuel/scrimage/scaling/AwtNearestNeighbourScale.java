package com.sksamuel.scrimage.scaling;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AwtNearestNeighbourScale implements Scale {

    public BufferedImage scale(BufferedImage in, int w, int h) {
        BufferedImage target = new BufferedImage(w, h, in.getType());
        Graphics2D g2 = (Graphics2D) target.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(in, 0, 0, w, h, null);
        g2.dispose();
        return target;
    }
}
