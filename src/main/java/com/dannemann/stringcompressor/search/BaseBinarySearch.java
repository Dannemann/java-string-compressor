package com.dannemann.stringcompressor.search;

/**
 * @see FourBitBinarySearch
 * @see FiveBitBinarySearch
 * @see SixBitBinarySearch
 */
public abstract class BaseBinarySearch {

	protected final byte[][] compressedData;
	protected final boolean prefixSearch;
	protected final byte[] charset;

	protected BaseBinarySearch(byte[][] compressedData, boolean prefixSearch, byte[] charset) {
		this.compressedData = compressedData;
		this.prefixSearch = prefixSearch;
		this.charset = charset;
	}

	public abstract int search(final byte[] key);

	public byte[][] getCompressedData() {
		return compressedData;
	}

	public boolean isPrefixSearch() {
		return prefixSearch;
	}

	public byte[] getCharset() {
		return charset;
	}

}
