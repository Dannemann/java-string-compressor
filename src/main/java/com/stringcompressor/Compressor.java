package com.stringcompressor;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Compressor {

	public boolean throwException = false;

	protected final byte[] lookupTable = new byte[128];
	protected final byte maxSupportedChars;

	public Compressor(int bitsPerChar) {
		maxSupportedChars = (byte) Math.pow(2, bitsPerChar);
	}

	abstract public byte[] compress(byte[] bytes);
	abstract public byte[] decompress(byte[] chars);

	public void charMappingChanged() {
		resetLookupTable();
		fillLookupTable();
	}

	protected void resetLookupTable() {
		Arrays.fill(lookupTable, (byte) -1);
	}

	protected void fillLookupTable() {
		byte i = 0;
		for (byte bite : getSupportedChars())
			lookupTable[bite] = i++;
	}

	protected byte[] getSupportedChars() {
		byte[] r = new byte[maxSupportedChars];

		for (byte i = 0; i < maxSupportedChars; i++)
			try {
				Field field = getClass().getField("char" + i);
				byte value = field.getByte(this);
				r[i] = value;
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		return r;
	}

}
