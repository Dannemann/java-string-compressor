package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Jean Dannemann Carone
 */
public abstract class AsciiCompressor {

	protected final byte[] supportedCharset;
	protected final byte[] lookupTable = new byte[128]; // ASCII range.

	/**
	 * To avoid duplicating strings and save memory, the compressor will modify
	 * the original input string, encoding it and making it unusable. To avoid
	 * this behavior and compress a copy of the original, set this to <code>true</code>.
	 */
	protected boolean preserveOriginal = true; // TODO: Set this false after updating benchmarks.

	/**
	 * Throw validation exceptions while compressing. Useful for debug but not
	 * recommended for production.
	 */
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
	 * Restores the original string from data compressed by {@link #compress}.
	 *
	 * @param compressed The compressed string byte array.
	 * @return A decompressed string byte array.
	 */
	public abstract byte[] decompress(byte[] compressed);

	protected abstract void validateSupportedCharset(byte[] supportedCharset);

	public void setPreserveOriginal(boolean preserveOriginal) {
		this.preserveOriginal = preserveOriginal;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	protected void encode(byte[] string, int len) {
		if (throwException)
			for (int i = 0; i < len; i++) {
				byte bite = string[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' (code " + bite + ") in \"" + new String(string, US_ASCII) + "\"");

				byte encoded = lookupTable[bite];

				if (encoded == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' (code " + bite + ") is not defined in the supported characters array. String: \"" + new String(string, US_ASCII) + "\"");

				string[i] = encoded;
			}
		else
			for (int i = 0; i < len; i++)
				string[i] = lookupTable[string[i] & 0x7F];
	}

}
