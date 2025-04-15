package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import java.nio.charset.StandardCharsets;

public class FourBitsCompressor extends Compressor {

	public static final byte[] DEFAULT_4BIT_CHARSET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ';', '#', '-', '+', '.', ','};

	public FourBitsCompressor() {
		super(DEFAULT_4BIT_CHARSET);
	}

	public FourBitsCompressor(byte[] supported4BitCharset) {
		super(supported4BitCharset);
	}

	/**
	 * <p>Compresses 2 characters into 1 byte (4 bits each).
	 * Only supports a set of 16 characters (4 bits).</p>
	 * <p>Compression rate: 50%</p>
	 * <p>Supported characters by default (from {@code char0} to {@code char15}): 0 1 2 3 4 5 6 7 8 9 ; # - + . ,</p>
	 * @param bytes String to be compressed.
	 * @return A compressed byte array.
	 */
	public byte[] compress(byte[] bytes) {
		int len = bytes.length;

		for (int i = 0; i < len; i++) {
			byte nibble = lookupTable[bytes[i] & 0x7F];

			if (nibble == -1 && throwException)
				throw new CharacterNotSupportedException(bytes[i]);

			bytes[i] = nibble;
		}

		int halfLen = len >> 1;
		byte[] outputArr = new byte[halfLen + (len & 1)];

		for (int i = 0; i < halfLen; i++)
			outputArr[i] = (byte) (bytes[2 * i] << 4 | bytes[2 * i + 1]);

		if ((len & 1) == 1)
			outputArr[halfLen] = bytes[len - 1];

		return outputArr;
	}

	/**
	 * Decompresses a 4-bit compressed string.
	 * @param chars The compressed string byte array.
	 * @return A decompressed byte array where each item is a .
	 */
	public byte[] decompress(byte[] chars) {
		var sb = new StringBuilder();

		for (var c : chars)
			sb.append((char) supportedCharset[(byte) ((c & 0xF0) >> 4)])
			  .append((char) supportedCharset[(byte) (c & 0x0F)]);

		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

}
