package com.stringcompressor;

import java.util.Arrays;

public abstract class AsciiCompressor {

	protected final byte[] supportedCharset;
	protected final byte[] lookupTable = new byte[128]; // ASCII range.

	protected boolean throwException;

	public AsciiCompressor(byte[] supportedCharset) {
		validateSupportedCharset(supportedCharset);
		this.supportedCharset = supportedCharset;

		Arrays.fill(lookupTable, (byte) -1);
		for (int i = 0, len = supportedCharset.length; i < len; i++)
			lookupTable[supportedCharset[i]] = (byte) i;
	}

	public abstract byte[] compress(byte[] str);

	/**
	 * Restores the original String from data compressed by {@link #compress}.
	 *
	 * @param compressed The compressed String byte array.
	 * @return A decompressed String byte array.
	 */
	public abstract byte[] decompress(byte[] compressed);

	protected abstract void validateSupportedCharset(byte[] supportedCharset);

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

}
