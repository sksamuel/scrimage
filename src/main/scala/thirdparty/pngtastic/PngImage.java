package thirdparty.pngtastic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a png image
 *
 * @author rayvanderborght
 */
public class PngImage {

    public static final long SIGNATURE = 0x89504e470d0a1a0aL;

    /** */
    private String fileName;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** */
    private List<PngChunk> chunks = new ArrayList<PngChunk>();

    public List<PngChunk> getChunks() {
        return this.chunks;
    }

    /** */
    private long width;

    public long getWidth() {
        return this.width;
    }

    /** */
    private long height;

    public long getHeight() {
        return this.height;
    }

    /** */
    private short bitDepth;

    public short getBitDepth() {
        return this.bitDepth;
    }

    /** */
    private short colorType;

    public short getColorType() {
        return this.colorType;
    }

    /** */
    private short interlace;

    public short getInterlace() {
        return this.interlace;
    }

    public void setInterlace(short interlace) {
        this.interlace = interlace;
    }

    /** */
    private PngImageType imageType;

    public PngImage() {
    }

    public PngImage(InputStream ins) {
        try {
            DataInputStream dis = new DataInputStream(ins);
            readSignature(dis);

            int length = 0;
            PngChunk chunk = null;

            do {
                length = getChunkLength(dis);

                byte[] type = getChunkType(dis);
                byte[] data = getChunkData(dis, length);
                long crc = getChunkCrc(dis);

                chunk = new PngChunk(type, data);

                if (!chunk.verifyCRC(crc)) {
                    throw new PngException("Corrupted file, crc check failed");
                }

                addChunk(chunk);
            } while (length > 0 && !PngChunk.IMAGE_TRAILER.equals(chunk.getTypeString()));
        } catch (IOException ignored) {
        } catch (PngException ignored) {
        }
    }

    public DataOutputStream writeDataOutputStream(OutputStream output) throws IOException {
        DataOutputStream outs = new DataOutputStream(output);
        outs.writeLong(PngImage.SIGNATURE);

        for (PngChunk chunk : chunks) {
            outs.writeInt(chunk.getLength());
            outs.write(chunk.getType());
            outs.write(chunk.getData());
            int i = (int) chunk.getCRC();
            outs.writeInt(i);
        }
        outs.close();

        return outs;
    }

    /** */
    public void addChunk(PngChunk chunk) {
        if (PngChunk.IMAGE_HEADER.equals(chunk.getTypeString())) {
            this.width = chunk.getWidth();
            this.height = chunk.getHeight();
            this.bitDepth = chunk.getBitDepth();
            this.colorType = chunk.getColorType();
            this.interlace = chunk.getInterlace();
        }
        this.chunks.add(chunk);
    }

    /** */
    public byte[] getImageData() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Write all the IDAT data
            for (PngChunk chunk : chunks) {
                if (chunk.getTypeString().equals("IDAT")) {
                    out.write(chunk.getData());
                }
            }
            return out.toByteArray();
        } catch (IOException e) {
            System.out.println("Couldn't get image data: " + e);
        }
        return null;
    }

    /** */
    public int getSampleBitCount() {
        this.imageType = (this.imageType == null) ? PngImageType.forColorType(this.colorType) : this.imageType;
        return this.imageType.channelCount() * this.bitDepth;
    }

    /* */
    private int getChunkLength(DataInputStream ins) throws IOException {
        return ins.readInt();
    }

    /* */
    private byte[] getChunkType(InputStream ins) throws PngException {
        return getChunkData(ins, 4);
    }

    /* */
    private byte[] getChunkData(InputStream ins, int length) throws PngException {
        byte[] data = new byte[length];
        try {
            int actual = ins.read(data);
            if (actual < length) {
                throw new PngException(String.format("Expected %d bytes but got %d", length, actual));
            }
        } catch (IOException e) {
            throw new PngException("Error reading chunk data", e);
        }

        return data;
    }

    /* */
    private long getChunkCrc(DataInputStream ins) throws IOException {
        int i = ins.readInt();
        long crc = i & 0x00000000ffffffffL; // Make it unsigned.
        return crc;
    }

    /* */
    private static void readSignature(DataInputStream ins) throws PngException, IOException {
        long signature = ins.readLong();
        if (signature != PngImage.SIGNATURE) {
            throw new PngException("Bad png signature");
        }
    }
}
