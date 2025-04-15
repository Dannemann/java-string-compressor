package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * <p>Performs 4-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 50%</p>
 * <p>Check {@link #compress} and {@link #decompress} for further details.</p>
 *
 * @author Jean Dannemann Carone
 */
public class FourBitsAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_4BIT_CHARSET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';', '#', '-', '+', '.', ','};

	/**
	 * Creates a 4-bit-per-character compressor with the default supported characters set.
	 */
	public FourBitsAsciiCompressor() {
		super(DEFAULT_4BIT_CHARSET);
	}

	/**
	 * Creates a 4-bit-per-character compressor with a custom characters set.
	 */
	public FourBitsAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset);
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

		if (throwException)
			for (int i = 0; i < len; i++) {
				byte bite = str[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' in \"" + new String(str, US_ASCII) + "\"");

				byte nibble = lookupTable[bite];

				if (nibble == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' is not defined in the supported characters array. String: \"" + new String(str, US_ASCII) + "\"");

				str[i] = nibble;
			}
		else
			for (int i = 0; i < len; i++)
				str[i] = lookupTable[str[i]];

		int halfLen = len >> 1;
		byte[] compressed = new byte[halfLen + (len & 1)];

		for (int i = 0; i < halfLen; i++)
			compressed[i] = (byte) (str[i << 1] << 4 | str[(i << 1) + 1]);

		if ((len & 1) == 1)
			compressed[halfLen] = str[len - 1];

		return compressed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] decompress(byte[] compressed) {
		int len = compressed.length;
		byte[] decompressed = new byte[len << 1];

		for (int i = 0, j = 0; i < len; i++) {
			byte bite = compressed[i];
			decompressed[j++] = supportedCharset[(bite & 0xF0) >> 4];
			decompressed[j++] = supportedCharset[bite & 0x0F];
		}

		return decompressed;
	}

	@Override
	protected void validateSupportedCharsetSize(byte[] supportedCharset) {
		int len = supportedCharset.length;
		if (len == 0 || len > 16)
			throw new CharacterNotSupportedException(
				"4-bit compressor supports a minimum of 1 and a maximum of 16 different characters. Currently " + len + ".");
	}

}
