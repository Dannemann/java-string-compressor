package com.dannemann.stringcompressor;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Performs 5-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 38%</p>
 *
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_5BIT_CHARSET = new byte[]{
		' ', '\'', ',', '-', '.', '@',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	public static final byte[] DEFAULT_5BIT_CHARSET_LOWERCASE = new byte[]{
		' ', '\'', ',', '-', '.', '@',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	public FiveBitAsciiCompressor() {
		super(DEFAULT_5BIT_CHARSET, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	public FiveBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	public FiveBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_5BIT_CHARSET, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	public FiveBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	public FiveBitAsciiCompressor(boolean throwException, boolean preserveOriginal) {
		super(DEFAULT_5BIT_CHARSET, throwException, preserveOriginal);
	}

	public FiveBitAsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal) {
		super(supportedCharset, throwException, preserveOriginal);
	}

	/**
	 * <p>Packs characters into chunks of 5 bits. Supports a set of 32 different characters (00000 to 11111).</p>
	 * <p>Compression rate: 38%</p>
	 * <p>See {@link #DEFAULT_5BIT_CHARSET} for the default set of supported characters.</p>
	 *
	 * @param string string to be compressed.
	 * @return A compressed byte array.
	 */
	@Override
	public final byte[] compress(final byte[] string) {
		final byte[] str = preserveOriginal ? string.clone() : string;
		final int len = string.length;

		encode(str, len);

		final int compressedLen = len * 5 + 7 >>> 3;
		final byte[] compressed = new byte[compressedLen + (-len >>> 31)];
		int buffer = 0;
		int bitsInBuffer = 0;
		int j = 0;

		for (int i = 0; i < len; i++) {
			buffer = buffer << 5 | str[i];
			bitsInBuffer += 5;

			if (bitsInBuffer >= 8)
				compressed[j++] = (byte) (buffer >>> (bitsInBuffer -= 8));
		}

		if (bitsInBuffer > 0) {
			compressed[j] = (byte) (buffer << 8 - bitsInBuffer);

			if (bitsInBuffer <= 3)
				compressed[compressedLen] |= 0x01;
		}

		return compressed;
	}

	public final byte[][] compress(final List<String> stringMass) {
		final byte[][] compressedMass = new byte[stringMass.size()][];

		Arrays.parallelSetAll(compressedMass, i -> {
			final byte[] compressed = compress(getBytes(stringMass.get(i)));
//			stringMass.set(i, null);
			return compressed;
		});

		return compressedMass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final byte[] decompress(final byte[] compressed) {
		final int compressedLen = compressed.length;

		if (compressedLen == 0)
			return new byte[0];

		final int cLenMinus = compressedLen - 1;
		final int dLen = cLenMinus * 8 / 5 - (compressed[cLenMinus] & 1);
		final byte[] decompressed = new byte[dLen];
		int buffer = 0;
		int bitsInBuffer = 0;

		for (int i = 0, j = 0; i < cLenMinus; i++) {
			buffer = buffer << 8 | compressed[i] & 0xFF;
			bitsInBuffer += 8;

			if (bitsInBuffer >= 5)
				decompressed[j++] = supportedCharset[buffer >>> (bitsInBuffer -= 5) & 0x1F];

			if (bitsInBuffer >= 5 && j < dLen)
				decompressed[j++] = supportedCharset[buffer >>> (bitsInBuffer -= 5) & 0x1F];
		}

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		standardCharsetValidation(supportedCharset, 5);
	}

}
