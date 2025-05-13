package com.dannemann.stringcompressor;

/**
 * <p>Performs 4-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 50%</p>
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressor extends AsciiCompressor {

	/**
	 * 4-bit character set supported by default: '#', '+', ',', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';'
	 */
	public static final byte[] DEFAULT_4BIT_CHARSET = {'#', '+', ',', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';'};

	/**
	 * Creates a new compressor instance using the default 4-bit character set ({@link #DEFAULT_4BIT_CHARSET}). Uses default settings
	 * for exception throwing ({@link #throwException} is {@code false}) and original data preservation ({@link #preserveOriginal} is {@code false}).
	 * @author Jean Dannemann Carone
	 */
	public FourBitAsciiCompressor() {
		super(DEFAULT_4BIT_CHARSET, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance with a user defined 4-bit character set. Uses default settings for exception throwing
	 * ({@link #throwException} is {@code false}) and original data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_4BIT_CHARSET} for an example of how to define it.
	 * @author Jean Dannemann Carone
	 * @see #supportedCharset
	 */
	public FourBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset, THROW_EXCEPTION_DEFAULT, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance using the default 4-bit character set ({@link #DEFAULT_4BIT_CHARSET}) and with a configurable
	 * exception-throwing behavior. Uses default setting for original data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @author Jean Dannemann Carone
	 * @see #throwException
	 */
	public FourBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_4BIT_CHARSET, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance with a user defined 4-bit character set and configurable exception-throwing behavior.
	 * Uses default setting for original data preservation ({@link #preserveOriginal} is {@code false}).
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_4BIT_CHARSET} for an example of how to define it.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @author Jean Dannemann Carone
	 * @see #supportedCharset
	 * @see #throwException
	 */
	public FourBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset, throwException, PRESERVE_ORIGINAL_DEFAULT);
	}

	/**
	 * Creates a new compressor instance using the default 4-bit character set ({@link #DEFAULT_4BIT_CHARSET}),
	 * with configurable exception-throwing behavior and original data preservation.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @param preserveOriginal If {@code true}, preserves the original input data by working on a copy; otherwise, modifies the input data directly.
	 * @author Jean Dannemann Carone
	 * @see #throwException
	 * @see #preserveOriginal
	 */
	public FourBitAsciiCompressor(boolean throwException, boolean preserveOriginal) {
		super(DEFAULT_4BIT_CHARSET, throwException, preserveOriginal);
	}

	/**
	 * Creates a new compressor instance with a user defined 4-bit character set, configurable exception-throwing behavior, and original data preservation.
	 * @param supportedCharset The custom set of supported characters. See {@link #DEFAULT_4BIT_CHARSET} for an example of how to define it.
	 * @param throwException If {@code true}, throws an exception on unsupported characters; otherwise, silently ignores them.
	 * @param preserveOriginal If {@code true}, preserves the original input data by working on a copy; otherwise, modifies the input data directly.
	 * @author Jean Dannemann Carone
	 * @see #supportedCharset
	 * @see #throwException
	 * @see #preserveOriginal
	 */
	public FourBitAsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal) {
		super(supportedCharset, throwException, preserveOriginal);
	}

	/**
	 * <p>Compresses 2 characters into 1 byte (4 bits each). Supports a set of 16 different characters (0000 to 1111).</p>
	 * <p>Compression rate: 50%</p>
	 * <p>See {@link #DEFAULT_4BIT_CHARSET} for the default set of supported characters. To use a custom character set,
	 * refer to any constructor that accepts the {@code byte[] supportedCharset} parameter.</p>
	 * @throws NullPointerException {@inheritDoc}
	 * @author Jean Dannemann Carone
	 */
	@Override
	public final byte[] compress(final byte[] string) {
		final byte[] str = preserveOriginal ? string.clone() : string;
		final int len = string.length;

		encode(str, len);

		final int halfLen = len >> 1;
		final byte[] compressed = new byte[halfLen + (len & 1) + (-len >>> 31)];

		for (int i = 0; i < halfLen; i++)
			compressed[i] = (byte) (str[i << 1] << 4 | str[(i << 1) + 1]);

		if ((len & 1) == 1) {
			compressed[halfLen] = str[len - 1];
			compressed[halfLen + 1] = 1;
		}

		return compressed;
	}

	/**
	 * Overloaded version of {@link #compress(byte[])}.
	 * @author Jean Dannemann Carone
	 */
	@Override
	public byte[] compress(final String string) {
		return compress(getBytes(string));
	}

	/**
	 * @author Jean Dannemann Carone
	 */
	@Override
	public final byte[] decompress(final byte[] compressed) {
		int cLenMinus = compressed.length - 1;

		if (cLenMinus <= 0)
			return new byte[0];

		final int odd = compressed[cLenMinus];
		final int dLen = odd == 1 ? (--cLenMinus << 1) + 1 : cLenMinus << 1;
		final byte[] decompressed = new byte[dLen];

		for (int i = 0, j = 0; i < cLenMinus; i++) {
			final byte bite = compressed[i];
			decompressed[j++] = supportedCharset[(bite & 0xF0) >> 4];
			decompressed[j++] = supportedCharset[bite & 0x0F];
		}

		if (odd == 1)
			decompressed[dLen - 1] = supportedCharset[compressed[cLenMinus]];

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		standardCharsetValidation(supportedCharset, 4);
	}

}
