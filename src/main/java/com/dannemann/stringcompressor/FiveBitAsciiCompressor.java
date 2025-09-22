package com.dannemann.stringcompressor;

/**
 * <p>Performs 5-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 38%</p>
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressor extends AsciiCompressor {

	/**
	 * 5-bit character set supported by default (ordered by ASCII): ' ', '\'', ',', '-', '.', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	 */
	public static final byte[] DEFAULT_5BIT_CHARSET = new byte[]{
		' ', '\'', ',', '-', '.', '@',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	/**
	 * 5-bit lowercase character set supported by default (ordered by ASCII): ' ', '\'', ',', '-', '.', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	 */
	public static final byte[] DEFAULT_5BIT_CHARSET_LOWERCASE = new byte[]{
		' ', '\'', ',', '-', '.', '@',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * Creates a new compressor instance using the default 5-bit character set ({@link #DEFAULT_5BIT_CHARSET}). Uses default settings
	 * for exception throwing ({@link #throwException} is {@code false}) and byte array data preservation ({@link #preserveOriginal} is {@code false}).
	 * @author Jean Dannemann Carone
	 */
	public FiveBitAsciiCompressor() {
		super(DEFAULT_5BIT_CHARSET, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance with a user defined 5-bit character set. Uses default settings for exception throwing
	 * ({@link #throwException} is {@code false}) and byte array data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_5BIT_CHARSET} for an example of how to define it.
	 * @author Jean Dannemann Carone
	 * @see #getSupportedCharset()
	 */
	public FiveBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance using the default 5-bit character set ({@link #DEFAULT_5BIT_CHARSET}) and with a configurable
	 * exception-throwing behavior. Uses default setting for byte array data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @author Jean Dannemann Carone
	 * @see #isThrowException()
	 */
	public FiveBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_5BIT_CHARSET, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance with a user defined 5-bit character set and configurable exception-throwing behavior.
	 * Uses default setting for byte array data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_5BIT_CHARSET} for an example of how to define it.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @author Jean Dannemann Carone
	 * @see #getSupportedCharset()
	 * @see #isThrowException()
	 */
	public FiveBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance using the default 5-bit character set ({@link #DEFAULT_5BIT_CHARSET}),
	 * with configurable exception-throwing behavior and byte array data preservation.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @param preserveOriginal If {@code true}, preserves the original input byte array by working on a copy; otherwise, modifies it directly.
	 * @author Jean Dannemann Carone
	 * @see #isThrowException()
	 * @see #isPreserveOriginal()
	 */
	public FiveBitAsciiCompressor(boolean throwException, boolean preserveOriginal) {
		super(DEFAULT_5BIT_CHARSET, throwException, preserveOriginal);
	}

	/**
	 * Creates a new compressor instance with a user defined 5-bit character set, configurable exception-throwing behavior, and byte array data preservation.
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_5BIT_CHARSET} for an example of how to define it.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @param preserveOriginal If {@code true}, preserves the original input byte array by working on a copy; otherwise, modifies it directly.
	 * @author Jean Dannemann Carone
	 * @see #getSupportedCharset()
	 * @see #isThrowException()
	 * @see #isPreserveOriginal()
	 */
	public FiveBitAsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal) {
		super(supportedCharset, throwException, preserveOriginal);
	}

	/**
	 * <p>Packs characters into chunks of 5 bits. Supports a set of 32 different characters (00000 to 11111).</p>
	 * <p>Compression rate: 38%</p>
	 * <p>See {@link #DEFAULT_5BIT_CHARSET} for the default set of supported characters. To use a custom character set,
	 * refer to any constructor that accepts the {@code byte[] supportedCharset} parameter.</p>
	 * @throws NullPointerException {@inheritDoc}
	 * @author Jean Dannemann Carone
	 */
	@Override
	public final byte[] compress(final byte[] string) {
		final byte[] str = preserveOriginal ? string.clone() : string;
		final int len = string.length;

		encode(str, len);

		final int compressedLen = (int) (len * 5L + 7 >> 3);
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

	/**
	 * <p>Overloaded version of {@link #compress(byte[])}.</p>
	 * <p>Whenever possible, use {@link #compress(byte[])} and avoid string instantiation.</p>
	 * @author Jean Dannemann Carone
	 */
	@Override
	public byte[] compress(final String string) {
		return compress(getBytes(string));
	}

	/**
	 * Restores the original string from data compressed by {@link #compress(byte[])}.
	 * @author Jean Dannemann Carone
	 */
	@Override
	public final byte[] decompress(final byte[] compressed) {
		final int compressedLen = compressed.length;

		if (compressedLen == 0)
			return new byte[0];

		final int cLenMinus = compressedLen - 1;
		final int dLen = (int) (cLenMinus * 8L / 5) - (compressed[cLenMinus] & 1);
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
