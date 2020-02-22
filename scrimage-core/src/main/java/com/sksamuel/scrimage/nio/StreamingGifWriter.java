package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.ImmutableImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Baseed on work by Elliot Kroo on 2009-04-25 and adapted into Scala and rewritten.
 * <p>
 * This work is licensed under the Creative Commons Attribution 3.0 Unported
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * <p>
 * Typical usage will look something like:
 * {{{
 * import com.sksamuel.scrimage.nio.StreamingGifWriter
 * val stream = StreamingGifWriter()
 * stream.prepareStream("/path/to/gif.gif", BufferedImage.TYPE_INT_ARGB)
 * stream.writeFrame(image0)
 * stream.writeFrame(image1)
 * stream.writeFrame(imageN)
 * stream.finish()
 * }}}
 */
public class StreamingGifWriter {

    private final Duration frameDelay;
    private final boolean infiniteLoop;

    public StreamingGifWriter(Duration frameDelay, boolean infiniteLoop) {
        this.frameDelay = frameDelay;
        this.infiniteLoop = infiniteLoop;
    }

    public StreamingGifWriter withFrameDelay(Duration delay) {
        return new StreamingGifWriter(delay, infiniteLoop);
    }

    public StreamingGifWriter withInfiniteLoop(boolean infiniteLoop) {
        return new StreamingGifWriter(frameDelay, infiniteLoop);
    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if
     * the requested node does not exist).
     *
     * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param nodeName the name of the child node.
     * @return the child node, if found or a new node created with the given name.
     */
    private IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            IIOMetadataNode node = (IIOMetadataNode) rootNode.item(i);
            if (node.getNodeName().equalsIgnoreCase(nodeName)) {
                return node;
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }

    interface GifStream {
        GifStream writeFrame(ImmutableImage image) throws IOException;

        GifStream writeFrame(ImmutableImage image, Duration delay) throws IOException;

        void finish() throws IOException;
    }

    GifStream prepareStream(String path, int imageType) throws IOException {
        return prepareStream(Paths.get(path), imageType);
    }

    GifStream prepareStream(Path path, int imageType) throws IOException {
        return prepareStream(path.toFile(), imageType);
    }

    GifStream prepareStream(File file, int imageType) throws IOException {

        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", (frameDelay.toMillis() / 10) + "");
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by Scrimage");

        IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = infiniteLoop ? 0 : 1;
        byte[] childobj = new byte[3];
        childobj[0] = 0x1;
        childobj[1] = (byte) (loop & 0xFF);
        childobj[2] = (byte) ((loop >> 8) & 0xFF);
        child.setUserObject(childobj);
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        FileImageOutputStream fos = new FileImageOutputStream(file);

        writer.setOutput(fos);
        writer.prepareWriteSequence(null);

        return new GifStream() {
            @Override
            public GifStream writeFrame(ImmutableImage image) throws IOException {
                writer.writeToSequence(new IIOImage(image.awt(), null, imageMetaData), imageWriteParam);
                return this;
            }

            @Override
            public GifStream writeFrame(ImmutableImage image, Duration delay) throws IOException {
                IIOMetadataNode rootOverride = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
                IIOMetadataNode graphicsControlExtensionNodeOverride = getNode(rootOverride, "GraphicControlExtension");
                graphicsControlExtensionNodeOverride.setAttribute("delayTime", (delay.toMillis() / 10L) + "");
                imageMetaData.setFromTree(metaFormatName, rootOverride);

                writer.writeToSequence(new IIOImage(image.awt(), null, imageMetaData), imageWriteParam);

                imageMetaData.setFromTree(metaFormatName, root);
                return this;
            }

            @Override
            public void finish() throws IOException {
                writer.endWriteSequence();
                writer.dispose();
                fos.close();
            }
        };
    }

}
