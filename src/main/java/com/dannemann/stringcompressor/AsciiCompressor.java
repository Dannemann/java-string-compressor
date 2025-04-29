package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.CharacterNotSupportedException;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Jean Dannemann Carone
 */
public abstract class AsciiCompressor {

	protected static final boolean PRESERVE_ORIGINAL_DEFAULT = false;
	protected static final boolean THROW_EXCEPTION_DEFAULT = false;

	// Fields:

	protected final byte[] supportedCharset;

	/**
	 * Throw validation exceptions while compressing. Useful for debugging but not recommended for production.
	 */
	protected final boolean throwException;

	/**
	 * To avoid duplicating strings and save memory, the compressor will modify
	 * the original input string, encoding it and making it unusable. To avoid
	 * this behavior and compress a copy of the original, set this to <code>true</code>.
	 */
	protected final boolean preserveOriginal;

	private final byte[] lookupTable = new byte[128]; // ASCII range.

	// Constructor:

	public AsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal) {
		validateSupportedCharset(supportedCharset);

		this.supportedCharset = supportedCharset;
		this.throwException = throwException;
		this.preserveOriginal = preserveOriginal;

		Arrays.fill(lookupTable, (byte) -1);
		for (int i = 0, len = supportedCharset.length; i < len; i++)
			lookupTable[supportedCharset[i]] = (byte) i;
	}

	// Abstract methods:

	public abstract byte[] compress(final byte[] string);

	/**
	 * Restores the original string from data compressed by {@link #compress}.
	 *
	 * @param compressed The compressed string byte array.
	 * @return A decompressed string byte array.
	 */
	public abstract byte[] decompress(final byte[] compressed);

	protected abstract void validateSupportedCharset(byte[] supportedCharset);

	// Protected interface:

	protected void standardCharsetValidation(byte[] supportedCharset, int numBits) {
		int len = supportedCharset.length;
		int maxChars = (int) Math.pow(2, numBits);

		if (len != maxChars)
			throw new CharacterNotSupportedException(
				numBits + "-bit compressor requires a set of exactly " + maxChars + " characters. Currently " + len + ".");

		for (int i = 0; i < len; i++)
			if (supportedCharset[i] < 0)
				throw new CharacterNotSupportedException(
					"Invalid character found in the custom supported charset: '" + (char) supportedCharset[i] + "' (code point " + supportedCharset[i] + ")");
	}

	protected void encode(final byte[] string, final int len) {
		if (throwException)
			for (int i = 0; i < len; i++) {
				final byte bite = string[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' (code point " + bite + ") in \"" + new String(string, US_ASCII) + "\"");

				final byte encoded = lookupTable[bite];

				if (encoded == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' (code point " + bite + ") is not defined in the supported characters array. String: \"" + new String(string, US_ASCII) + "\"");

				string[i] = encoded;
			}
		else
			for (int i = 0; i < len; i++)
				string[i] = lookupTable[string[i] & 0x7F];
	}

}
