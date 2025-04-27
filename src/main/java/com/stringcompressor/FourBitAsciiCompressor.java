package com.stringcompressor;

/**
 * <p>Performs 4-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: 50%</p>
 *
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_4BIT_CHARSET = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';', '#', '-', '+', '.', ','};

	public FourBitAsciiCompressor() {
		super(DEFAULT_4BIT_CHARSET);
	}

	public FourBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset);
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
	public final byte[] compress(byte[] str) {
		final int len = str.length;

		if (preserveOriginal)
			str = str.clone();

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
	 * {@inheritDoc}
	 */
	@Override
	public final byte[] decompress(final byte[] compressed) {
		int cLen = compressed.length - 1;

		if (cLen <= 0)
			return new byte[0];

		final int odd = compressed[cLen];
		final int dLen = odd == 1 ? (--cLen << 1) + 1 : cLen << 1;
		final byte[] decompressed = new byte[dLen];

		for (int i = 0, j = 0; i < cLen; i++) {
			final byte bite = compressed[i];
			decompressed[j++] = supportedCharset[(bite & 0xF0) >> 4];
			decompressed[j++] = supportedCharset[bite & 0x0F];
		}

		if (odd == 1)
			decompressed[dLen - 1] = supportedCharset[compressed[cLen]];

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		standardCharsetValidation(supportedCharset, 4, 16);
	}

}
