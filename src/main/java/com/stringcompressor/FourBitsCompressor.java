package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;

import java.nio.charset.StandardCharsets;

public class FourBitsCompressor extends Compressor {

	// Default character set supported for 4-bit compression.
	public byte char0 = '0';
	public byte char1 = '1';
	public byte char2 = '2';
	public byte char3 = '3';
	public byte char4 = '4';
	public byte char5 = '5';
	public byte char6 = '6';
	public byte char7 = '7';
	public byte char8 = '8';
	public byte char9 = '9';
	public byte char10 = ';';
	public byte char11 = '#';
	public byte char12 = '-';
	public byte char13 = '+';
	public byte char14 = '.';
	public byte char15 = ',';

	public FourBitsCompressor() {
		super(4);
		charMappingChanged();
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
	private byte[] decompress(byte[] chars) {
		var sb = new StringBuilder();

		for (var c : chars) {
			nibbleToAscii((byte) ((c & 0xF0) >> 4), sb); // First nibble.
			nibbleToAscii((byte) (c & 0x0F), sb); // Second nibble.
		}

		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	protected void nibbleToAscii(byte nibble, StringBuilder stringBuilder) {
		switch (nibble) {
			case 0xA: stringBuilder.append(';'); break;
			case 0xB: stringBuilder.append('#'); break;
			case 0xC: stringBuilder.append('-'); break;
			case 0xD: stringBuilder.append('+'); break;
			case 0xE: stringBuilder.append('.'); break;
			case 0xF: stringBuilder.append(','); break;
//			default: if (throwError) throw new RuntimeException("Character " + bytes[i] + " is not supported.");
		}
	}


}
