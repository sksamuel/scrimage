package com.sksamuel.scrimage.composite;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.Composite;

import java.awt.*;

public class AlphaComposite implements Composite {

    private final double alpha;

    public AlphaComposite(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public void apply(AwtImage src, AwtImage overlay) {
        Graphics2D g2 = (Graphics2D) src.awt.getGraphics();
        g2.setComposite(java.awt.AlphaComposite.SrcOver.derive((float) alpha));
        g2.drawImage(overlay.awt, 0, 0, null);
        g2.dispose();
    }
}