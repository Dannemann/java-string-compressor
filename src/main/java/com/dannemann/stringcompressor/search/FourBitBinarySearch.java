package com.dannemann.stringcompressor.search;

import com.dannemann.stringcompressor.FourBitAsciiCompressor;

import static com.dannemann.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;

/**
 * <p>Performs binary search on data compressed by {@link FourBitAsciiCompressor}. The data must have been sorted prior
 * to compression.</p>
 * <p>If {@code prefixSearch} is {@code true}, the method returns the first element whose prefix matches the specified
 * key. Otherwise, it looks for an exact match. When multiple elements share the same prefix, the first matching element
 * is returned (just as in the exact‐match search).</p>
 * <p>Null elements are considered to come after any character, in the same way that Z comes after A. This is because
 * the {@code compressedData} array typically has extra space to accommodate new entries, so unused slots (nulls) are
 * placed at the end.</p>
 * <p>Note that character ordering depends on the sequence defined in your custom charset (via
 * {@code supportedCharset}), which is passed to the compressor constructor (see
 * {@link FourBitAsciiCompressor#FourBitAsciiCompressor(byte[])}). If no custom charset is provided, compressors use a
 * default charset ordered by ASCII.</p>
 * @author Jean Dannemann Carone
 * @see FourBitAsciiCompressor#DEFAULT_4BIT_CHARSET
 */
public final class FourBitBinarySearch extends BaseBinarySearch {

	/**
	 * Creates a binary search object for data compressed with the default character set {@link FourBitAsciiCompressor#DEFAULT_4BIT_CHARSET}.
	 * @param compressedData The mass of compressed strings to search through.
	 * @param prefixSearch If {@code true}, searches for elements starting with the provided key prefix (must be unique).
	 * @author Jean Dannemann Carone
	 * @see FourBitBinarySearch#FourBitBinarySearch(byte[][], boolean, byte[])
	 */
	public FourBitBinarySearch(byte[][] compressedData, boolean prefixSearch) {
		super(compressedData, prefixSearch, DEFAULT_4BIT_CHARSET);
	}

	/**
	 * Creates a binary search object.
	 * @param compressedData The mass of compressed strings to search through.
	 * @param prefixSearch If {@code true}, searches for elements starting with the provided key prefix (must be unique).
	 * @param charset Character set used to compress {@code compressedData}.
	 * @author Jean Dannemann Carone
	 */
	public FourBitBinarySearch(byte[][] compressedData, boolean prefixSearch, byte[] charset) {
		super(compressedData, prefixSearch, charset);
	}

	/**
	 * <p>Performs a binary search on the provided compressed data array to locate the specified key.</p>
	 * <p>The compressed data is expected to be produced by {@link FourBitAsciiCompressor} and must be sorted before
	 * compression for this search to work correctly. The search is performed directly on the compressed form without
	 * decompressing the entire dataset, enabling fast lookups in large in-memory compressed collections.</p>
	 * <p>The method returns the index of the matching element if found; otherwise, it returns
	 * {@code -(insertion point) - 1}, following the contract of {@link java.util.Arrays#binarySearch}.</p>
	 * @param key The uncompressed key to search for, as a byte array.
	 * @return The index of the search key if it is found; otherwise, {@code -(insertion point) - 1}.
	 * @author Jean Dannemann Carone
	 */
	@Override
	public int search(final byte[] key) {
		final int dataLength = compressedData.length;

		if (dataLength == 0)
			return -1;

		final int keyLen = key.length;
		int low = 0;
		int high = dataLength - 1;

		while (low <= high) {
			final int mid = low + high >>> 1;
			final byte[] compStr = compressedData[mid];
			int cmp = 0;

			if (compStr == null)
				cmp = 1;
			else {
				final int odd;
				final int dLen;
				int cLenMinus = compStr.length - 1;

				if (cLenMinus >= 0) {
					odd = compStr[cLenMinus];
					dLen = odd == 1 ? (--cLenMinus << 1) + 1 : cLenMinus << 1;
				} else {
					odd = 0;
					dLen = 0;
				}

				int j = 0;

				for (int i = 0; i < cLenMinus && j < keyLen; i++) {
					final byte bite = compStr[i];

					if ((cmp = charset[(bite & 0xF0) >> 4] - key[j++]) != 0 ||
						j < keyLen &&
						(cmp = charset[bite & 0x0F] - key[j++]) != 0)
						break;
				}

				if (cmp == 0 && odd == 1 && j < keyLen)
					cmp = charset[compStr[cLenMinus]] - key[j];

				if (cmp == 0) {
					if (prefixSearch && keyLen <= dLen)
						return mid;

					cmp = dLen - keyLen;
				}
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

}
