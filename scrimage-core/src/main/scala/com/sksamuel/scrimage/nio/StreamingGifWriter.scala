package com.sksamuel.scrimage.nio

import java.io.File
import java.nio.file.{ Paths, Path }

import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.{ IIOImage, ImageIO, ImageTypeSpecifier }

import com.sksamuel.scrimage.Image

import scala.concurrent.duration._

/**
 * Baseed on work by Elliot Kroo on 2009-04-25 and adapted into Scala and rewritten.
 *
 * This work is licensed under the Creative Commons Attribution 3.0 Unported
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
 * Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 *
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
case class StreamingGifWriter(frameDelay: Duration = 1.second, infiniteLoop: Boolean = true) {

  def withFrameDelay(delay: Duration): StreamingGifWriter = copy(frameDelay = delay)
  def withInfiniteLoop(infiniteLoop: Boolean): StreamingGifWriter = copy(infiniteLoop = infiniteLoop)

  /**
   * Returns an existing child node, or creates and returns a new child node (if
   * the requested node does not exist).
   *
   * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
   * @param nodeName the name of the child node.
   *
   * @return the child node, if found or a new node created with the given name.
   */
  private def getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode = {
    val nodes = for (i <- 0 until rootNode.getLength) yield rootNode.item(i).asInstanceOf[IIOMetadataNode]
    nodes.find(_.getNodeName.equalsIgnoreCase(nodeName)).getOrElse {
      val node = new IIOMetadataNode(nodeName)
      rootNode.appendChild(node)
      node
    }
  }

  abstract class GifStream {
    def writeFrame(image: Image): GifStream
    def writeFrame(image: Image, delay: Duration): GifStream
    def finish(): Unit
  }

  def prepareStream(path: String, imageType: Int): GifStream = prepareStream(Paths.get(path), imageType)

  def prepareStream(path: Path, imageType: Int): GifStream = prepareStream(path.toFile, imageType)

  def prepareStream(file: File, imageType: Int): GifStream = {

    import scala.collection.JavaConverters._

    val writer = ImageIO.getImageWritersBySuffix("gif").asScala.next
    val imageWriteParam = writer.getDefaultWriteParam()

    val imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType)
    val imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam)

    val metaFormatName = imageMetaData.getNativeMetadataFormatName()

    val root = imageMetaData.getAsTree(metaFormatName).asInstanceOf[IIOMetadataNode]

    val graphicsControlExtensionNode = getNode(root, "GraphicControlExtension")

    graphicsControlExtensionNode.setAttribute("disposalMethod", "none")
    graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE")
    graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE")
    graphicsControlExtensionNode.setAttribute("delayTime", (frameDelay.toMillis / 10).toString)
    graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0")

    val commentsNode = getNode(root, "CommentExtensions")
    commentsNode.setAttribute("CommentExtension", "Created by Scrimage")

    val appEntensionsNode = getNode(root, "ApplicationExtensions")
    val child = new IIOMetadataNode("ApplicationExtension")

    child.setAttribute("applicationID", "NETSCAPE")
    child.setAttribute("authenticationCode", "2.0")

    val loop = if (infiniteLoop) 0 else 1
    child.setUserObject(Array[Byte](0x1, (loop & 0xFF).toByte, ((loop >> 8) & 0xFF).toByte))
    appEntensionsNode.appendChild(child)

    imageMetaData.setFromTree(metaFormatName, root)

    val fos = new FileImageOutputStream(file)

    writer.setOutput(fos)
    writer.prepareWriteSequence(null)

    new GifStream {

      def writeFrame(image: Image): GifStream = {
        writer.writeToSequence(new IIOImage(image.awt, null, imageMetaData), imageWriteParam)
        this
      }

      def writeFrame(image: Image, delay: Duration): GifStream = {
        val rootOverride = imageMetaData.getAsTree(metaFormatName).asInstanceOf[IIOMetadataNode]
        val graphicsControlExtensionNodeOverride = getNode(rootOverride, "GraphicControlExtension")
        graphicsControlExtensionNodeOverride.setAttribute("delayTime", (delay.toMillis / 10).toString)
        imageMetaData.setFromTree(metaFormatName, rootOverride)

        writer.writeToSequence(new IIOImage(image.awt, null, imageMetaData), imageWriteParam)

        imageMetaData.setFromTree(metaFormatName, root)
        this
      }

      def finish(): Unit = {
        writer.endWriteSequence()
        writer.dispose()
        fos.close()
      }

    }
  }

}
