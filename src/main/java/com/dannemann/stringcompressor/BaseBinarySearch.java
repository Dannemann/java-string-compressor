package com.dannemann.stringcompressor;

public abstract class BaseBinarySearch {

	protected final byte[][] compressedMass;
	protected final boolean prefixSearch;
	protected final byte[] charset;

	public BaseBinarySearch(byte[][] compressedMass, boolean prefixSearch, byte[] charset) {
		this.compressedMass = compressedMass;
		this.prefixSearch = prefixSearch;
		this.charset = charset;
	}

	public abstract int search(final byte[] key);

	public byte[][] getCompressedMass() {
		return compressedMass;
	}

	public boolean isPrefixSearch() {
		return prefixSearch;
	}

	public byte[] getCharset() {
		return charset;
	}

}
