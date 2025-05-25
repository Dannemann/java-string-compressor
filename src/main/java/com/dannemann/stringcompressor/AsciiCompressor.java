package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.CharacterNotSupportedException;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * The base ASCII compressor.
 * @author Jean Dannemann Carone
 */
public abstract class AsciiCompressor {

	// Fields:

	protected static final boolean THROW_EXCEPTION_DEFAULT = false;
	protected static final boolean PRESERVE_ORIGINAL_DEFAULT = false;

	protected final byte[] supportedCharset;
	protected final boolean throwException;
	protected final boolean preserveOriginal;

	private final byte[] lookupTable = new byte[128];

	// Constructor:

	/**
	 * @see #getSupportedCharset()
	 * @see #isThrowException()
	 * @see #isPreserveOriginal()
	 */
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

	/**
	 * @param string String to be compressed.
	 * @return A compressed byte array.
	 * @throws NullPointerException If {@code string} is null.
	 */
	public abstract byte[] compress(final byte[] string);

	/**
	 * Overloaded version of {@link #compress(byte[])}.
	 */
	public abstract byte[] compress(final String string);

	/**
	 * @param compressed The compressed string byte array.
	 * @return A decompressed string byte array.
	 */
	public abstract byte[] decompress(final byte[] compressed);

	protected abstract void validateSupportedCharset(byte[] supportedCharset);

	// Protected interface:

	protected void standardCharsetValidation(byte[] supportedCharset, int numBits) {
		int maxChars = (int) Math.pow(2, numBits);

		if (supportedCharset.length != maxChars)
			throw new CharacterNotSupportedException(
				numBits + "-bit compressor requires a set of exactly " + maxChars + " characters. Currently " + supportedCharset.length + ".");

		for (byte bite : supportedCharset)
			if (bite < 0)
				throw new CharacterNotSupportedException(
					"Invalid character found in the custom supported charset: '" + (char) bite + "' (code point " + bite + ")");
	}

	/**
	 * @author Jean Dannemann Carone
	 */
	protected void encode(final byte[] string, final int len) {
		if (throwException)
			for (int i = 0; i < len; i++) {
				final byte bite = string[i];

				if (bite < 0)
					throw new CharacterNotSupportedException(
						"Only ASCII characters are supported. Invalid '" + (char) bite + "' with code point " + bite + " in string (maybe incomplete): \"" + new String(string, ISO_8859_1) + "\"");

				final byte encoded = lookupTable[bite];

				if (encoded == -1)
					throw new CharacterNotSupportedException(
						"Character '" + (char) bite + "' with code point " + bite + " is not defined in the supported characters array. Source string is (maybe incomplete): \"" + new String(string, ISO_8859_1) + "\"");

				string[i] = encoded;
			}
		else
			for (int i = 0; i < len; i++)
				string[i] = lookupTable[string[i] & 0x7F];
	}

	// Utils:

	/**
	 * <p>Fastest way to get bytes from an ASCII String.</p>
	 * <p>If {@code string} is null, returns null.</p>
	 * <p>This method effectively do: {@code string.getBytes(ISO_8859_1)}<br/>
	 * <a href="https://cl4es.github.io/2021/02/23/Faster-Charset-Decoding.html">To understand why, click here.</a></p>
	 * @param string The target string.
	 * @return The resultant byte array.
	 * @author Jean Dannemann Carone
	 */
	public static byte[] getBytes(final String string) {
		return string != null ? string.getBytes(ISO_8859_1) : null;
	}

	// Getters:

	/**
	 * <p>The set of characters accepted by this compressor. Compressors can only encode characters defined
	 * explicitly by this charset, which is typically chosen based on specific application requirements.</p>
	 * <p>Refer to the default character sets provided by this library: {@link FourBitAsciiCompressor#DEFAULT_4BIT_CHARSET},
	 * {@link FiveBitAsciiCompressor#DEFAULT_5BIT_CHARSET}, and {@link SixBitAsciiCompressor#DEFAULT_6BIT_CHARSET}.</p>
	 * @author Jean Dannemann Carone
	 */
	public byte[] getSupportedCharset() {
		return supportedCharset;
	}

	/**
	 * <p>Determines if a validation exception should be thrown when encountering unsupported characters during
	 * compression. Generally not recommended for production environments, as it's preferable to silently ignore
	 * invalid characters without interrupting the entire compression process.</p>
	 * <p>Useful for testing and debugging.</p>
	 * @author Jean Dannemann Carone
	 */
	public boolean isThrowException() {
		return throwException;
	}

	/**
	 * <p>When dealing directly with byte[] string representation, the compressor will modify the original array, encoding
	 * it and making it unusable. To avoid this behavior and compress a copy of the original, set this to {@code true}.</p>
	 * <p>Useful for testing and debugging on a zero-allocation environment, when the source is also a byte array.</p>
	 * @author Jean Dannemann Carone
	 */
	public boolean isPreserveOriginal() {
		return preserveOriginal;
	}

}
