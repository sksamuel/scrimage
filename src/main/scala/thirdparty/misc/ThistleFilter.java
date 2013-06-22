package thirdparty.misc;

import thirdparty.romainguy.BlendComposite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;

/**
 * @author Sarah Schölzel
 */
public class ThistleFilter {

    public BufferedImage filter(BufferedImage bi) {
        if (bi.getSampleModel().getDataType() != DataBuffer.TYPE_INT)
            throw new UnsupportedOperationException();

        BufferedImage dstImg = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        BufferedImage tmp = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D gtmp = tmp.createGraphics();
        gtmp.setColor(new Color(17, 24, 66));
        gtmp.fill(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));

        BufferedImage tmp2 = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics2D gtmp2 = tmp2.createGraphics();
        gtmp2.setColor(new Color(220, 227, 84));
        gtmp2.fill(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));

        //Blending Mode: Lighten, 55%
        gtmp2.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.LIGHTEN, 0.55f));
        gtmp2.drawImage(tmp, 0, 0, bi.getWidth(), bi.getHeight(), null);

        //texture
        BufferedImage texture;
        try {
            texture = ImageIO.read(getClass().getResource("/com/sksamuel/scrimage/filter/texture_old_square.jpg"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //Blending Mode: Multiply, 75%
        tmp = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        gtmp = tmp.createGraphics();
        gtmp.drawImage(texture, 0, 0, bi.getWidth(), bi.getHeight(), null);
        gtmp.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.MULTIPLY, 0.75f));
        gtmp.drawImage(tmp2, 0, 0, bi.getWidth(), bi.getHeight(), null);

        //Blending Mode: Overlay, 65%
        Graphics2D gdst = dstImg.createGraphics();
        gdst.drawImage(bi, 0, 0, null);
        gdst.setComposite(BlendComposite.getInstance(BlendComposite.BlendingMode.OVERLAY, 1f));
        gdst.drawImage(tmp, 0, 0, bi.getWidth(), bi.getHeight(), null);

        return dstImg;
    }
}
