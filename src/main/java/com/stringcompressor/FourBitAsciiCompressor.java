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

		if (true) { // TODO: Finish this.
			byte[] temp = new byte[len];
			System.arraycopy(str, 0, temp, 0, len);
			str = temp;
		}

		if (throwException)
			for (int i = 0; i < len; i++) {
				byte bite = str[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' (code " + bite + ") in \"" + new String(str, US_ASCII) + "\"");

				byte nibble = lookupTable[bite];

				if (nibble == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' (code " + bite + ") is not defined in the supported characters array. String: \"" + new String(str, US_ASCII) + "\"");

				str[i] = nibble;
			}
		else
			for (int i = 0; i < len; i++)
				str[i] = lookupTable[str[i] & 0x7F];

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

//	public long[] compressLong(byte[] str) {
//		int len = str.length;
//		byte[] strCopy = new byte[len];
//
//		System.arraycopy(str, 0, strCopy, 0, len);
//
//		byte[] compressed = new byte[(int) Math.ceil(len / 64)];
//
//		if (throwException)
//			for (int i = 0; i < len; i++) {
//				byte bite = strCopy[i];
//
//				if (bite < 0)
//					throw new CharacterNotSupportedException(
//						"Only ASCII characters are supported. Invalid '" + (char) bite + "' (code " + bite + ") in \"" + new String(strCopy, US_ASCII) + "\"");
//
//				byte nibble = lookupTable[bite];
//
//				if (nibble == -1)
//					throw new CharacterNotSupportedException(
//						"Character '" + (char) bite + "' (code " + bite + ") is not defined in the supported characters array. String: \"" + new String(strCopy, US_ASCII) + "\"");
//
//				strCopy[i] = nibble;
//			}
//		else {
//			int remainingSpace = 16;
//			for (int i = 0; i < len; i++) {
//
//				strCopy[i] = lookupTable[strCopy[i] & 0x7F];
//			}
//		}
//
//
////		for (int i = 0; i < compressed.length; i++)
////			compressed[i] = (byte) (strCopy[i<<1] << 4 | strCopy[(i << 1) + 1]);
//
//		if ((len & 1) == 1) {
//			compressed[halfLen] = strCopy[len - 1];
//			compressed[halfLen + 1] = 1;
//		}
//
//		return compressed;
//	}

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
