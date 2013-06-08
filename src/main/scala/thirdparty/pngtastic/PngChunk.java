package thirdparty.pngtastic;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

/**
 * Represents a PNG chunk
 *
 * @author rayvanderborght
 */
public class PngChunk {

	/** critical chunks */
	public static final String IMAGE_HEADER		= "IHDR";
	public static final String PALETTE			= "PLTE";
	public static final String IMAGE_DATA		= "IDAT";
	public static final String IMAGE_TRAILER	= "IEND";

	/** ancilliary chunks */
	public static final String TRANSPARANCY					= "TRNS";
	public static final String COLOR_SPACE_INFO				= "CHRM";
	public static final String IMAGE_GAMA					= "GAMA";
	public static final String EMBEDDED_ICCP_PROFILE		= "ICCP";
	public static final String SIGNIFICANT_BITS				= "SBIT";
	public static final String STANDARD_RGB					= "SRGB";
	public static final String TEXTUAL_DATA					= "TEXT";
	public static final String COMPRESSED_TEXTUAL_DATA		= "ZTXT";
	public static final String INTERNATIONAL_TEXTUAL_DATA	= "ITXT";
	public static final String BACKGROUND_COLOR				= "BKGD";
	public static final String IMAGE_HISTOGRAM				= "HIST";
	public static final String PHYSICAL_PIXEL_DIMENSIONS	= "PHYS";
	public static final String SUGGESTED_PALETTE			= "SPLT";
	public static final String IMAGE_LAST_MODIFICATION_TIME	= "TIME";

	private final byte[] type;
	private final byte[] data;

	/** */
	public PngChunk(byte[] type, byte[] data) {
		this.type = (type == null) ? null : type.clone();
		this.data = (data == null) ? null : data.clone();
	}

	/** */
	public String getTypeString() {
		try {
			return new String(this.type, "UTF8");
		} catch(UnsupportedEncodingException e) {
			return "";
		}
	}

	/** */
	public byte[] getType() {
		return (this.type == null) ? null : this.type.clone();
	}

	/** */
	public byte[] getData() {
		return (this.data == null) ? null : this.data.clone();
	}

	/** */
	public int getLength() {
		return this.data.length;
	}

	/** */
	public long getWidth() {
		return this.getUnsignedInt(0);
	}

	/** */
	public long getHeight() {
		return this.getUnsignedInt(4);
	}

	/** */
	public short getBitDepth() {
		return this.getUnsignedByte(8);
	}

	/** */
	public short getColorType() {
		return this.getUnsignedByte(9);
	}

	/** */
	public short getCompression() {
		return this.getUnsignedByte(10);
	}

	/** */
	public short getFilter() {
		return this.getUnsignedByte(11);
	}

	/** */
	public short getInterlace() {
		return this.getUnsignedByte(12);
	}

	/** */
	public void setInterlace(byte interlace) {
		this.data[12] = interlace;
	}

	/** */
	public long getUnsignedInt(int offset) {
		long value = 0;
		for (int i = 0; i < 4; i++) {
			value += (this.data[offset + i] & 0xff) << ((3 - i) * 8);
		}

		return value;
	}

	/** */
	public short getUnsignedByte(int offset) {
		return (short) (this.data[offset] & 0x00ff);
	}

	/** */
	public boolean isCritical() {
		String type = this.getTypeString().toUpperCase();
		return type.equals(IMAGE_HEADER)
			|| type.equals(PALETTE)
			|| type.equals(IMAGE_DATA)
			|| type.equals(IMAGE_TRAILER);
	}

	/** */
	public boolean isRequired() {
		return this.isCritical()
			|| TRANSPARANCY.equals(this.getTypeString().toUpperCase())
			|| IMAGE_GAMA.equals(this.getTypeString().toUpperCase())
			|| COLOR_SPACE_INFO.equals(this.getTypeString().toUpperCase());
	}

	/** */
	public boolean verifyCRC(long crc) {
		return (this.getCRC() == crc);
	}

	/** */
	public long getCRC() {
		CRC32 crc32 = new CRC32();
		crc32.update(this.type);
		crc32.update(this.data);

		return crc32.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[').append(this.getTypeString()).append(']').append('\n');
		if (PngChunk.IMAGE_HEADER.equals(this.getTypeString().toUpperCase())) {
			result.append("Size:        ").append(this.getWidth()).append('x').append(this.getHeight()).append('\n');
			result.append("Bit depth:   ").append(this.getBitDepth()).append('\n');
			result.append("Image type:  ").append(this.getColorType()).append(" (").append(PngImageType.forColorType(this.getColorType())).append(")\n");
			result.append("Color type:  ").append(this.getColorType()).append('\n');
			result.append("Compression: ").append(this.getCompression()).append('\n');
			result.append("Filter:      ").append(this.getFilter()).append('\n');
			result.append("Interlace:   ").append(this.getInterlace());
		}
		if (PngChunk.TEXTUAL_DATA.equals(this.getTypeString().toUpperCase())) {
			result.append("Text:        ").append(new String(this.data));
		}
		if (PngChunk.IMAGE_DATA.equals(this.getTypeString().toUpperCase())) {
			result.append("Image Data:  ")
				.append("length=").append(this.getLength()).append(", data=");

//			for (byte b : this.data)
//				result.append(String.format("%x", b));

			result.append(", crc=").append(this.getCRC());
		}

		return result.toString();
	}
}