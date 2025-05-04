//package com.dannemann.stringcompressor.search;
//
//import com.dannemann.stringcompressor.FiveBitAsciiCompressor;
//
//import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
//import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
//
///**
// * Executes binary search on compressed data by {@link FiveBitAsciiCompressor}.
// * Very useful if you have massive compressed data stored in memory; you can search straight from it.
// * Data must be lexicographically ordered ASC.
// */
//public class FiveBitBinarySearch {
//
//	public static int binarySearch(final byte[][] compressedMass, final byte[] key) {
//		final byte[] charset = DEFAULT_5BIT_CHARSET;
//		final int keyLen = key.length;
//		int low = 0, high = compressedMass.length - 1;
//
//		while (low <= high) {
//			final int mid = (low + high) >>> 1;
//			final byte[] data = compressedMass[mid];
//			final int last = data.length - 1;
//			final int dLen = last >= 0 ? (last * 8) / 5 - (data[last] & 1) : 0;
//
//			int cmp = 0;
//			final int minLen = dLen < keyLen ? dLen : keyLen;
//			int buffer = 0, bits = 0, j = 0;
//
//			for (int i = 0; i < last && j < minLen; i++) {
//				buffer = (buffer << 8) | (data[i] & 0xFF);
//				bits += 8;
//
//				if (bits >= 5) {
//					bits -= 5;
//					byte cv = charset[(buffer >>> bits) & 0x1F];
//					cmp = cv - key[j++];
//					if (cmp != 0) break;
//
//					if (bits >= 5 && j < minLen) {
//						bits -= 5;
//						cv = charset[(buffer >>> bits) & 0x1F];
//						cmp = cv - key[j++];
//						if (cmp != 0) break;
//					}
//				}
//			}
//
//			if (cmp == 0) {
//				cmp = dLen - keyLen;
//			}
//
//			if (cmp < 0) low = mid + 1;
//			else if (cmp > 0) high = mid - 1;
//			else return mid;
//		}
//
//		return -(low + 1);
//	}
//
//
//
//	public static int binarySearch(final byte[][] compressedMass, final String key) {
//		return binarySearch(compressedMass, getBytes(key));
//	}
//
//}
