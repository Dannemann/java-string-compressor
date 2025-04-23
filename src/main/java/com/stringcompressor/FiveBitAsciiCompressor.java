package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FiveBitAsciiCompressor extends AsciiCompressor {

	public static final byte[] DEFAULT_5BIT_CHARSET = new byte[]{
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		' ', '.', ',', '\'', '-'};

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

	@Override
	public byte[] compress(byte[] str) {
		int len = str.length;

		if (len == 0)
			return str;

		byte[] str2 = new byte[len];

		System.arraycopy(str, 0, str2, 0, len);

		if (throwException)
			for (int i = 0; i < len; i++) {
				byte bite = str2[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' (code " + bite + ") in \"" + new String(str2, US_ASCII) + "\"");

				byte nibble = lookupTable[bite];

				if (nibble == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' (code " + bite + ") is not defined in the supported characters array. String: \"" + new String(str2, US_ASCII) + "\"");

				str2[i] = nibble;
			}
		else
			for (int i = 0; i < len; i++)
				str2[i] = lookupTable[str2[i] & 0x7F];

		int cLen = (int) Math.ceil(len * .625) + 1;
		byte[] compressed = new byte[cLen];
		int available = 8;
		byte bucket = 0;
		int j = 0;

		for (int i = 0; i < len; i++) {
			byte bite = str2[i];

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

	@Override
	public byte[] decompress(byte[] compressed) {
		int len = compressed.length;

		if (len == 0)
			return compressed;

		int excess = 0;
		byte bucket = 0;
		int hint = compressed[len - 1];
		byte[] decompressed = new byte[(int) Math.floor((len - 1) / .625 - (hint & 1))];

		for (int i = 0, j = 0; i < len - 1; i++) {
			byte bite = compressed[i];

			if (excess > 0) {
				decompressed[j++] = supportedCharset[(byte) (bucket | ((bite & 0xFF) >>> 8 - excess))];

				if (j >= decompressed.length)
					break;
			}

			int bits = 5;

			if (excess < 4)
				decompressed[j++] = supportedCharset[(byte) (bite << excess + 24 >>> 27)];
			else
				bits = 0;

			bits += excess;

			if (bits == 8)
				excess = 0;
			else {
				excess = (5 - (8 - bits));
				bucket = (byte) (((bite << bits + 24) >>> bits + 24) << excess);
			}
		}

		return decompressed;
	}

	@Override
	protected void validateSupportedCharset(byte[] supportedCharset) {
	}

}
