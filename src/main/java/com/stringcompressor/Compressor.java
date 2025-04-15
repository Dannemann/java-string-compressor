package com.stringcompressor;

import java.util.Arrays;

public abstract class Compressor {

	protected final byte[] supportedCharset;
	protected final byte[] lookupTable = new byte[128];

	protected boolean throwException = true;

	public Compressor(byte[] supportedCharset) {
		this.supportedCharset = supportedCharset;

		Arrays.fill(lookupTable, (byte) -1);
		for (int i = 0, l = supportedCharset.length; i < l; i++)
			lookupTable[supportedCharset[i]] = (byte) i;
	}

	public abstract byte[] compress(byte[] bytes);
	public abstract byte[] decompress(byte[] chars);

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

}
