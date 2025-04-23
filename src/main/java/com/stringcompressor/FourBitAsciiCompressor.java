package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

/**
 * <p>Performs 4-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 50%</p>
 * <p>Check {@link #compress} and {@link #decompress} for further details.</p>
 *
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_4BIT_CHARSET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';', '#', '-', '+', '.', ','};

	public FourBitAsciiCompressor() {
		super(DEFAULT_4BIT_CHARSET);
	}

	public FourBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset);
	}

	public FourBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset);
		this.throwException = throwException;
	}

	public FourBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_4BIT_CHARSET);
		this.throwException = throwException;
	}

	/**
	 * <p>Compresses 2 characters into 1 byte (4 bits each). Supports a set of 16 different characters (0000 to 1111).</p>
	 * <p>Compression rate: 50%</p>
	 * <p>See {@link #DEFAULT_4BIT_CHARSET} for the default set of supported characters.</p>
	 *
	 * @param str String to be compressed.
	 * @return A compressed byte array.
	 */
	@Override
	public byte[] compress(byte[] str) {
		int len = str.length;

		if (preserveOriginal) {
			byte[] temp = new byte[len];
			System.arraycopy(str, 0, temp, 0, len);
			str = temp;
		}

		encode(str, len);

		int halfLen = len >> 1;
		byte[] compressed = new byte[halfLen + (len & 1) + 1];

		for (int i = 0; i < halfLen; i++)
			compressed[i] = (byte) (str[i << 1] << 4 | str[(i << 1) + 1]);

		if ((len & 1) == 1) {
			compressed[halfLen] = str[len - 1];
			compressed[halfLen + 1] = 1;
		}

		return compressed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] decompress(byte[] compressed) {
		int charsLen = compressed.length - 1;
		int odd = compressed[charsLen];
		int decompLen = odd == 1 ? (--charsLen << 1) + 1 : charsLen << 1;
		byte[] decompressed = new byte[decompLen];

		for (int i = 0, j = 0; i < charsLen; i++) {
			byte bite = compressed[i];
			decompressed[j++] = supportedCharset[(bite & 0xF0) >> 4];
			decompressed[j++] = supportedCharset[bite & 0x0F];
		}

		if (odd == 1)
			decompressed[decompLen - 1] = supportedCharset[compressed[charsLen]];

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		int len = supportedCharset.length;

		if (len == 0 || len > 16)
			throw new CharacterNotSupportedException(
				"4-bit compressor supports a minimum of 1 and a maximum of 16 different characters. Currently " + len + ".");

		for (int i = 0; i < len; i++)
			if (supportedCharset[i] < 0)
				throw new CharacterNotSupportedException(
					"Invalid character found in the custom supported charset: '" + (char) supportedCharset[i] + "' (code " + supportedCharset[i] + ")");
	}

}
