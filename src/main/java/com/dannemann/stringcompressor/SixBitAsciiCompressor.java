package com.dannemann.stringcompressor;

/**
 * <p>Performs 6-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 25%</p>
 * @author Jean Dannemann Carone
 */
public class SixBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_6BIT_CHARSET = new byte[]{
		' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		':', ';', '<', '=', '>', '?', '@',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'[', ']', '_', '{', '}'
	};

	public static final byte[] DEFAULT_6BIT_CHARSET_LOWERCASE = new byte[]{
		' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		':', ';', '<', '=', '>', '?', '@', '[', ']', '_',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'{', '}'
	};

	public SixBitAsciiCompressor() {
		super(DEFAULT_6BIT_CHARSET, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	public SixBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	public SixBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_6BIT_CHARSET, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	public SixBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	public SixBitAsciiCompressor(boolean throwException, boolean preserveOriginal) {
		super(DEFAULT_6BIT_CHARSET, throwException, preserveOriginal);
	}

	public SixBitAsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal) {
		super(supportedCharset, throwException, preserveOriginal);
	}

	/**
	 * <p>Packs characters into chunks of 6 bits. Supports a set of 64 different characters (000000 to 111111).</p>
	 * <p>Compression rate: 25%</p>
	 * <p>See {@link #DEFAULT_6BIT_CHARSET} for the default set of supported characters.</p>
	 * @throws NullPointerException {@inheritDoc}
	 */
	@Override
	public final byte[] compress(final byte[] string) {
		final byte[] str = preserveOriginal ? string.clone() : string;
		final int len = string.length;

		encode(str, len);

		final int compressedLen = len * 6 + 7 >>> 3;
		final byte[] compressed = new byte[compressedLen + (-len >>> 31)];
		int buffer = 0;
		int bitsInBuffer = 0;
		int j = 0;

		for (int i = 0; i < len; i++) {
			buffer = buffer << 6 | str[i];
			bitsInBuffer += 6;

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

	/**
	 * Overloaded version of {@link #compress(byte[])}.
	 */
	@Override
	public byte[] compress(final String string) {
		return compress(getBytes(string));
	}

	@Override
	public final byte[] decompress(final byte[] compressed) {
		final int compressedLen = compressed.length;

		if (compressedLen == 0)
			return new byte[0];

		final int cLenMinus = compressedLen - 1;
		final int dLen = cLenMinus * 8 / 6 - (compressed[cLenMinus] & 1);
		final byte[] decompressed = new byte[dLen];
		int buffer = 0;
		int bitsInBuffer = 0;

		for (int i = 0, j = 0; i < cLenMinus; i++) {
			buffer = buffer << 8 | compressed[i] & 0xFF;
			bitsInBuffer += 8;

			if (bitsInBuffer >= 6)
				decompressed[j++] = supportedCharset[buffer >>> (bitsInBuffer -= 6) & 0x3F];

			if (bitsInBuffer >= 6 && j < dLen)
				decompressed[j++] = supportedCharset[buffer >>> (bitsInBuffer -= 6) & 0x3F];
		}

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		standardCharsetValidation(supportedCharset, 6);
	}

}
