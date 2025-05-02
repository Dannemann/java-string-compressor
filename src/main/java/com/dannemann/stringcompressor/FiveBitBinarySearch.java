package com.dannemann.stringcompressor;

import java.nio.charset.StandardCharsets;

import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;

/**
 * Executes binary search on compressed data by {@link FiveBitAsciiCompressor}.
 * Useful if you have massive data store in memory.
 * Data must be lexicographically ordered ASC.
 */
public class FiveBitBinarySearch {

	static String[] words = {"A", "ABACUS", "ABB", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON", "HYPNOSIS", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};


	public static void main(String[] args) {
		FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor();
		String toFind = "B";

		byte[][] mass = new byte[31][];
		int i = 0;
		for (String word : words) {
			mass[i++] = compressor.compress(word.getBytes(StandardCharsets.US_ASCII));
		}

		for (byte[] m : mass) {
			System.out.println(new String(compressor.decompress(m), StandardCharsets.US_ASCII));
		}

		int result = binarySearch(mass, toFind.getBytes(StandardCharsets.US_ASCII));

		System.out.println(result);
	}

	// 000001 00
	// 0010 0000
	// 11 000100
	// 000101


	public static int binarySearch(byte[][] compressedMass, byte[] key) {
		int keyLen = key.length;
		int arrLen = compressedMass.length;
		int low = 0;
		int high = arrLen - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1; // Avoiding overflow.
			byte[] compressed = compressedMass[mid];
			final int compressedLen = compressed.length;
			final int cLenMinus = compressedLen - 1;
			final int dLen = cLenMinus * 8 / 5 - (compressed[cLenMinus] & 1);
			int buffer = 0;
			int bitsInBuffer = 0;
			Integer compareResult = null;

			for (int i = 0, j = 0; i < cLenMinus; i++) {
				buffer = buffer << 8 | compressed[i] & 0xFF;
				bitsInBuffer += 8;

				if (bitsInBuffer >= 5 && j < keyLen) {
					byte decompressedByte = DEFAULT_5BIT_CHARSET[buffer >>> (bitsInBuffer -= 5) & 0x1F];
					byte keyByte = key[j++];
					if (decompressedByte != keyByte) {
						compareResult = decompressedByte - keyByte;
						break;
					}
				}

				if (bitsInBuffer >= 5 && j < keyLen) {
					byte decompressedByte = DEFAULT_5BIT_CHARSET[buffer >>> (bitsInBuffer -= 5) & 0x1F];
					byte keyByte = key[j++];
					if (decompressedByte != keyByte) {
						compareResult = decompressedByte - keyByte;
						break;
					}
				}
			}

			if (compareResult == null)
				compareResult = dLen - keyLen;

			if (compareResult < 0) {
				low = mid + 1;
			} else if (compareResult > 0) {
				high = mid - 1;
			} else {
				return mid; // Found.
			}
		}

		return -(low + 1); // Key not found.
	}

	public static int compareStrings(String a, String b) {
		int minLen = Math.min(a.length(), b.length());
		for (int i = 0; i < minLen; i++) {
			char c1 = a.charAt(i);
			char c2 = b.charAt(i);
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return a.length() - b.length();
	}

//	private static byte[] extractValuesForComparison(byte[][] array, int arrayLength, int index) {
//		// 5-bit compression pattern:
//		// 00000 000    First pattern.
//		// 01 00010 0
//		// 0011 0010
//		// 0 00101 00
//		// 110 00111
//		// 01000 010    First pattern.
//		// 01 01010 0
//		// 1011 0110
//		// 0 01101 01
//		// 110 01111
//		// 10000 100    First pattern.
//		// 01 10010 1
//		// 0011...
//
//		Byte prevBits = index - 1 < 0 ? null : array[index - 1];
//		byte bits = array[index];
//		Byte nextBits = index + 1 >= arrayLength ? null : array[index + 1];
//		int pattern = index % 5;
//
//		return switch (pattern) {
//			case 0 -> extract1stPattern(bits, nextBits);
//			case 1 -> extract2ndPattern(prevBits, bits, nextBits);
//			case 2 -> extract3rdPattern(prevBits, bits, nextBits);
//			case 3 -> extract4thPattern(prevBits, bits, nextBits);
//			case 4 -> extract5thPattern(prevBits, bits);
//			default ->
//				throw new RuntimeException("Could not recognize bit pattern. Double check if the arguments are correct: " +
//					"arrayLength=" + arrayLength + ", index=" + index);
//		};
//	}

	// 00000 000
	private static byte[] extract1stPattern(byte bits, Byte nextBits) {
		byte[] values;
		if (nextBits != null) {
			values = new byte[2];
			values[1] = (byte) (((bits & 0x07) << 2) | ((nextBits & 0xC0) >> 6));
		} else {
			values = new byte[1];
		}
		values[0] = (byte) (bits >>> 3);
		return values;
	}

	//01 00010 0
	private static byte[] extract2ndPattern(Byte prevBits, byte bits, Byte nextBits) {
		byte[] values;
		if (nextBits != null) {
			values = new byte[3];
			values[2] = (byte) (((bits & 0x01) << 4) | ((nextBits & 0xF0) >> 4));
		} else {
			values = new byte[2];
		}
		values[1] = (byte) ((bits & 0x3E) >> 1); // TODO: If this is the last index of the array, how will we know if the middle bits mean something?
		values[0] = (byte) (((prevBits & 0x07) << 2) | ((bits & 0xC0) >> 6));
		return values;
	}

	// 0011 0010
	private static byte[] extract3rdPattern(Byte prevBits, byte bits, Byte nextBits) {
		byte[] values;
		if (nextBits != null) {
			values = new byte[2];
			values[1] = (byte) (((bits & 0x0F) << 1) | ((nextBits & 0x80) >> 7));
		} else {
			values = new byte[1];
		}
		values[0] = (byte) ((prevBits & 0x01 << 4) | (bits & 0xF0) >> 4);
		return values;
	}

	// 0 00101 00
	private static byte[] extract4thPattern(Byte prevBits, byte bits, Byte nextBits) {
		byte[] values;
		if (nextBits != null) {
			values = new byte[3];
			values[2] = (byte) (((bits & 0x03) << 3) | ((nextBits & 0xE0) >> 5));
		} else {
			values = new byte[2];
		}
		values[1] = (byte) ((bits & 0x7C) >> 2); // TODO: If this is the last index of the array, how will we know if the middle bits mean something?
		values[0] = (byte) (((prevBits & 0x0F) << 1) | ((bits & 0x80) >> 1));
		return values;
	}

	// 110 00111
	private static byte[] extract5thPattern(Byte prevBits, byte bits) {
		byte[] values = new byte[2];
		values[0] = (byte) (((prevBits & 0x03) << 3) | ((bits & 0xE0)) >> 5);
		values[1] = (byte) (bits & 0x1F);
		return values;
	}

}
