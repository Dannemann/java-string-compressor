package com.dannemann.stringcompressor;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;

/**
 * <p>Performs binary search (including prefix search) on data compressed by {@link FiveBitAsciiCompressor}.
 * Particularly useful when searching large amounts of compressed data stored in memory.</p>
 * <p>The data must have been sorted prior to compression.</p>
 * <p>Note that character ordering depends on the sequence defined in your custom charset (via {@code supportedCharset}),
 * which is passed to the compressor constructor (see {@link FiveBitAsciiCompressor#FiveBitAsciiCompressor(byte[])}).
 * If no custom charset is provided, compressors use a default charset ordered by ASCII.</p>
 * @author Jean Dannemann Carone
 */
public final class FiveBitBinarySearch {

	/**
	 * <p>Performs a binary search on the provided compressed data array to locate the specified key.</p>
	 * <p>The compressed data is expected to be produced by {@link FiveBitAsciiCompressor} and must be sorted before
	 * compression for this search to work correctly. The search is performed directly on the compressed form without
	 * decompressing the entire dataset, enabling fast lookups in large in-memory compressed collections.</p>
	 * <p>If {@code prefixSearch} is set to {@code true}, the method searches for an element whose prefix matches the
	 * specified key. Otherwise, it searches for an exact match. If there are multiple elements with the same prefix, the
	 * first matching element is returned.</p>
	 * <p>The method returns the index of the matching element if found; otherwise, it returns
	 * {@code -(insertion point) - 1}, following the contract of {@link java.util.Arrays#binarySearch}.</p>
	 * @param compressedMass The array of compressed byte arrays to search through.
	 * @param key The uncompressed key to search for, as a byte array.
	 * @param prefixSearch If {@code true}, searches for elements starting with the provided key prefix (must be unique).
	 * @return The index of the search key if it is found; otherwise, {@code -(insertion point) - 1}.
	 */
	public static int search(final byte[][] compressedMass, final byte[] key, boolean prefixSearch) {
		final int cLen = compressedMass.length;
		if (cLen == 0)
			return -1;

		final int keyLen = key.length;
		int low = 0;
		int high = cLen - 1;

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

			if (cmp == 0) {
				if (prefixSearch && keyLen <= dLen)
					return mid;

				cmp = dLen - keyLen;
			}

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}

		return -(low + 1);
	}

	/**
	 * Overloaded version of {@link #search(byte[][], byte[], boolean)} where parameter {@code prefixSearch = false}.
	 */
	public static int search(final byte[][] compressedMass, final byte[] key) {
		return search(compressedMass, key, false);
	}

	/**
	 * Overloaded version of {@link #search(byte[][], byte[], boolean)} where parameter {@code prefixSearch = false}.
	 */
	public static int search(final byte[][] compressedMass, final String key) {
		return search(compressedMass, getBytes(key));
	}

	/**
	 * Overloaded version of {@link #search(byte[][], byte[], boolean)} where parameter {@code prefixSearch = true}.
	 */
	public static int prefixSearch(final byte[][] compressedMass, final byte[] key) {
		return search(compressedMass, key, true);
	}

	/**
	 * Overloaded version of {@link #search(byte[][], byte[], boolean)} where parameter {@code prefixSearch = true}.
	 */
	public static int prefixSearch(final byte[][] compressedMass, final String key) {
		return prefixSearch(compressedMass, getBytes(key));
	}

}
