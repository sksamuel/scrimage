package com.sksamuel.scrimage.composite;

import com.sksamuel.scrimage.AwtImage;
import com.sksamuel.scrimage.Composite;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;

class BlenderComposite implements Composite {

    private final BlendingMode mode;
    private final double alpha;

    BlenderComposite(BlendingMode mode, double alpha) {
        this.mode = mode;
        this.alpha = alpha;
    }

    @Override
    public void apply(AwtImage src, AwtImage overlay) {
        Graphics2D g2 = (Graphics2D) src.awt.getGraphics();
        g2.setComposite(BlendComposite.getInstance(mode, (float) alpha));
        g2.drawImage(overlay.awt, 0, 0, null);
        g2.dispose();
    }
}