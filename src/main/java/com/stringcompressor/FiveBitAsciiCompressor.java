package com.stringcompressor;

/**
 * <p>Performs 5-bit-per-ASCII-character encoding and decoding.</p>
 * <p>Compression rate: %</p> // TODO: Fill compression rate.
 *
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_5BIT_CHARSET = new byte[]{
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		' ', '.', ',', '\'', '-', '@'};

	public FiveBitAsciiCompressor() {
		super(DEFAULT_5BIT_CHARSET);
	}

	public FiveBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset);
	}

	public FiveBitAsciiCompressor(byte[] supportedCharset, boolean throwException) {
		super(supportedCharset);
		this.throwException = throwException;
	}

	public FiveBitAsciiCompressor(boolean throwException) {
		super(DEFAULT_5BIT_CHARSET);
		this.throwException = throwException;
	}

	/**
	 * <p>Packs characters into chunks of 5 bits. Supports a set of 32 different characters (00000 to 11111).</p>
	 * <p>Compression rate: %</p> // TODO: Fill compression rate.
	 * <p>See {@link #DEFAULT_5BIT_CHARSET} for the default set of supported characters.</p>
	 *
	 * @param str string to be compressed.
	 * @return A compressed byte array.
	 */
	@Override
	public byte[] compress(byte[] str) {
		int dLen = str.length;

		if (preserveOriginal) {
			byte[] temp = new byte[dLen];
			System.arraycopy(str, 0, temp, 0, dLen);
			str = temp;
		}

		if (dLen == 0)
			return str;

		encode(str, dLen);

		// This is the bit pattern applied by the algorithm:
		// 00000 000
		// 01 00010 0
		// 0011 0010
		// 0 00101 00
		// 110 00111
		// 01000 010
		// 01 01010 0
		// 1011 0110
		// 0 01101 01
		// 110 01111
		// ...

		int cLen = (int) Math.ceil(dLen * .625) + 1;
		byte[] compressed = new byte[cLen];
		int j = 0;
		int available = 8;
		byte bucket = 0;

		for (int i = 0; i < dLen; i++) {
			byte bite = str[i];

			if (available >= 5) {
				compressed[j] |= bite;
				compressed[j] <<= 3 - (8 - available);
				compressed[j] |= bucket;
				bucket = 0;

				if ((available -= 5) == 0) {
					available = 8;
					j++;
				}
			} else {
				compressed[j] |= (byte) ((bite & 0xFF) >> 5 - available);
				compressed[j] |= bucket;
				available = 8 - (5 - available);
				bucket = (byte) (bite << available);
				j++;
			}
		}

		compressed[j] |= bucket;

		if (available > 4 && available < 8)
			compressed[cLen - 1] = 1;

		return compressed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] decompress(byte[] compressed) {
		int cLen = compressed.length;

		if (cLen == 0)
			return compressed;

		int cLenMinus = cLen - 1;
		int dLen = (int) Math.floor(cLenMinus / .625 - (compressed[cLenMinus] & 1));
		byte[] decompressed = new byte[dLen];
		int excess = 0;
		byte bucket = 0;

		for (int i = 0, j = 0; i < cLenMinus; i++) {
			byte bite = compressed[i];

			if (excess > 0) {
				decompressed[j++] = supportedCharset[bucket | (bite & 0xFF) >>> 8 - excess];

				if (j >= dLen)
					break;
			}

			int collected = 5;

			if (excess < 4)
				decompressed[j++] = supportedCharset[bite << excess + 24 >>> 27];
			else
				collected = 0;

			collected += excess;

			if (collected == 8)
				excess = 0;
			else {
				excess = 5 - (8 - collected);
				bucket = (byte) (bite << collected + 24 >>> collected + 24 << excess);
			}
		}

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
		standardCharsetValidation(supportedCharset, 5, 32);
	}

}
