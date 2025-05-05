package com.dannemann.stringcompressor;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;

/**
 * <p>Performs binary search on data compressed by {@link FiveBitAsciiCompressor}.
 * Particularly useful when searching large amounts of compressed data stored in memory.</p>
 * <p>The data must have been sorted prior to compression.</p>
 * <p>Note that the character ordering depends on the sequence defined in your custom charset (via {@code supportedCharset})
 * passed to the compressor constructor (see {@link FiveBitAsciiCompressor#FiveBitAsciiCompressor(byte[])}).
 * Compressors use a default charset ordered by ASCII if no custom charset is provided.</p>
 * @author Jean Dannemann Carone
 */
public final class FiveBitBinarySearch {

	public static int search(final byte[][] compressedMass, final byte[] key) {
		final int keyLen = key.length;
		int low = 0;
		int high = compressedMass.length - 1;

		while (low <= high) {
			final int mid = low + high >>> 1;
			final byte[] compStr = compressedMass[mid];
			final int last = compStr.length - 1;
			final int dLen = last >= 0 ? last * 8 / 5 - (compStr[last] & 1) : 0;
			final int minLen = Math.min(dLen, keyLen);
			int cmp = 0;
			int buffer = 0;
			int bits = 0;

			for (int i = 0, j = 0; i < last && j < minLen; i++) {
				buffer = buffer << 8 | compStr[i] & 0xFF;
				bits += 8;

				if (bits >= 5) {
					byte cv = DEFAULT_5BIT_CHARSET[buffer >>> (bits -= 5) & 0x1F];
					cmp = cv - key[j++];
					if (cmp != 0)
						break;

					if (bits >= 5 && j < minLen) {
						cv = DEFAULT_5BIT_CHARSET[buffer >>> (bits -= 5) & 0x1F];
						cmp = cv - key[j++];
						if (cmp != 0)
							break;
					}
				}
			}

			if (cmp == 0)
				cmp = dLen - keyLen;

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}

		return -(low + 1);
	}

	public static int search(final byte[][] compressedMass, final String key) {
		return search(compressedMass, getBytes(key));
	}

}
