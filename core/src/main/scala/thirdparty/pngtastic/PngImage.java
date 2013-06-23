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

    private PngImageType imageType;
    private short interlace;
    private short colorType;
    private long width;
    private long height;
    private short bitDepth;
    private List<PngChunk> chunks = new ArrayList<PngChunk>();

    public List<PngChunk> getChunks() {
        return this.chunks;
    }

    public long getWidth() {
        return this.width;
    }

    public long getHeight() {
        return this.height;
    }

    public short getBitDepth() {
        return this.bitDepth;
    }

    public short getColorType() {
        return this.colorType;
    }

    public short getInterlace() {
        return this.interlace;
    }

    public void setInterlace(short interlace) {
        this.interlace = interlace;
    }

    public PngImage() {
    }

    public PngImage(InputStream ins) {
        try {
            DataInputStream dis = new DataInputStream(ins);
            readSignature(dis);

            int length;
            PngChunk chunk;

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

    public int getSampleBitCount() {
        this.imageType = (this.imageType == null) ? PngImageType.forColorType(this.colorType) : this.imageType;
        return this.imageType.channelCount() * this.bitDepth;
    }

    private int getChunkLength(DataInputStream ins) throws IOException {
        return ins.readInt();
    }

    private byte[] getChunkType(InputStream ins) throws PngException {
        return getChunkData(ins, 4);
    }

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
        return i & 0x00000000ffffffffL;
    }

    /* */
    private static void readSignature(DataInputStream ins) throws PngException, IOException {
        long signature = ins.readLong();
        if (signature != PngImage.SIGNATURE) {
            throw new PngException("Bad png signature");
        }
    }
}
