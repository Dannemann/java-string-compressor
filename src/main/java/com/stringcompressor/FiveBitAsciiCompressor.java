package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FiveBitAsciiCompressor extends AsciiCompressor {

	public FiveBitAsciiCompressor(byte[] supportedCharset) {
		super(supportedCharset);
	}

	@Override
	public byte[] compress(byte[] str) {
		int len = str.length;
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

		byte[] compressed = new byte[len / 8 * 5];
		int available = 8;
		byte bucket = 0;
		boolean bucketFull = false;
		for (int i = 0, j = 0; i < len; i++) {
			byte bite = str2[i];

			if (available >= 5) {
				compressed[j] |= bite;
				compressed[j] <<= 3 - (8 - available);

//				if (bucketFull) {
				compressed[j] |= bucket;
//					bucketFull = false;
				bucket = 0;
//				}

				if ((available -= 5) == 0) {
					available = 8;
					j++;
				}
			} else {
				int rShifts = 5 - available;
				compressed[j] |= (byte) ((bite & 0xFF) >> rShifts);

//				if (bucketFull)
				compressed[j] |= bucket;

				int lShifts = 8 - rShifts;
				bucket = (byte) (bite << lShifts);
				bucketFull = true;

				available = lShifts;
				j++;
			}
		}

//		if ((len & 1) == 1) {
//			compressed[halfLen] = strCopy[len - 1];
//			compressed[halfLen + 1] = 1;
//		}

		return compressed;
	}

//	byte[] bitmasks = {0x00, };


	@Override
	public byte[] decompress(byte[] compressed) {
		int excess = 0;
		byte bucket = 0;

		byte[] decompressed = new byte[compressed.length / 5 * 8];

		for (int i = 0, j = 0, len = compressed.length; i < len; i++) {
			byte bite = compressed[i];

			if (excess > 0)
				decompressed[j++] = (byte) (bucket | ((bite & 0xFF) >>> 8 - excess));

			int bits = 5;

			if (excess < 4)
				decompressed[j++] |= (byte) (bite << excess + 24 >>> 27);
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
