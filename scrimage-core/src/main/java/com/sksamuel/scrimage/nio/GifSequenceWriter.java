package com.sksamuel.scrimage.nio;

import com.sksamuel.scrimage.Image;

import javax.imageio.*;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Baseed on work by Elliot Kroo on 2009-04-25 and adapted into Scala and rewritten.
 * <p>
 * This work is licensed under the Creative Commons Attribution 3.0 Unported
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
public class GifSequenceWriter {

    private final long frameDelayMillis;
    private final boolean infiniteLoop;

    public GifSequenceWriter(long frameDelayMillis, boolean infiniteLoop) {
        this.frameDelayMillis = frameDelayMillis;
        this.infiniteLoop = infiniteLoop;
    }

    public GifSequenceWriter() {
        this(1000, true);
    }

    public GifSequenceWriter withFrameDelay(long frameDelayMillis) {
        return new GifSequenceWriter(frameDelayMillis, infiniteLoop);
    }

    public GifSequenceWriter withInfiniteLoop(boolean infiniteLoop) {
        return new GifSequenceWriter(frameDelayMillis, infiniteLoop);
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

        IIOMetadataNode[] nodes = new IIOMetadataNode[rootNode.getLength()];

        for (int i = 0; i < rootNode.getLength(); i++) {
            nodes[i] = (IIOMetadataNode) rootNode.item(i);
        }

        return Arrays.stream(nodes)
                .filter(n -> n.getNodeName().equalsIgnoreCase(nodeName))
                .findFirst().orElseGet(() -> {
                    IIOMetadataNode node = new IIOMetadataNode(nodeName);
                    rootNode.appendChild(node);
                    return node;
                });
    }

    public Path output(Image[] images, Path path) throws IOException {
        return Files.write(path, bytes(images));
    }

    public Path output(Image[] images, File file) throws IOException {
        return Files.write(file.toPath(), bytes(images));
    }

    public Path output(Image[] images, String path) throws IOException {
        return Files.write(Paths.get(path), bytes(images));
    }

    public byte[] bytes(Image[] images) throws IOException {

        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(images[0].awt().getType());
        IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", (frameDelayMillis / 10) + "");
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by Scrimage");

        IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop;
        if (infiniteLoop)
            loop = 0;
        else
            loop = 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos)) {

                writer.setOutput(output);
                writer.prepareWriteSequence(null);

                for (Image image : images) {
                    writer.writeToSequence(new IIOImage(image.awt(), null, imageMetaData), imageWriteParam);
                }

                writer.endWriteSequence();
                return baos.toByteArray();
            }
        }
    }
}