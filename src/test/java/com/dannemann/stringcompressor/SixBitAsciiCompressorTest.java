package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET;
import static com.dannemann.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET_LOWERCASE;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
class SixBitAsciiCompressorTest extends BaseTest {

	@Test
	void usageExample() {
		// A string to be compressed. Whenever possible, prefer working directly with byte[] to avoid creating String objects.
		byte[] inputStr = AsciiCompressor.getBytes("HELLO, COMPRESSOR");

		// Creates a compressor with the default supported character set (see SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET).
		AsciiCompressor compressor = new SixBitAsciiCompressor();
//		AsciiCompressor customCompressor = new SixBitAsciiCompressor(new byte[]{/* custom charset */}, true, true);

		byte[] compressed = compressor.compress(inputStr);
		byte[] decompressed = compressor.decompress(compressed);

		assertEquals("HELLO, COMPRESSOR", new String(decompressed, US_ASCII));

		// If preserveOriginal is false, this will fail because inputStr has been modified.
//		assertArrayEquals(inputStr, decompressed);
	}

	@Test
	void validCustomCharsetTest() {
		byte[] customCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62, 63};
		new SixBitAsciiCompressor(customCharset);
	}

	@Test
	void excessCustomCharsetTest() {
		byte[] customCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62, 63, 64};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customCharset));
		assertEquals("6-bit compressor requires a set of exactly 64 characters. Currently 65.", e.getMessage());
	}

	@Test
	void missingCustomCharsetTest() {
		byte[] customCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customCharset));
		assertEquals("6-bit compressor requires a set of exactly 64 characters. Currently 63.", e.getMessage());
	}

	@Test
	void invalidCustomCharsetTest() {
		byte[] customCharset = new byte[]{
			-1, 0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
	}

	@Test
	void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"Ç\"", e.getMessage());
	}

	@Test
	void invalidCharCompressTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'|'}));
		assertEquals("Character '|' (code point 124) is not defined in the supported characters array. String: \"|\"", e.getMessage());
	}

	@Test
	void compressDecompressSmallStringTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true, true);
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 500000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_6BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void compressDecompressBigStringTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true, true);
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 10000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_6BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void compressDecompressSmallStringLowercaseTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(DEFAULT_6BIT_CHARSET_LOWERCASE, true, true);
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 50000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_6BIT_CHARSET_LOWERCASE);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void compressDecompressBigStringLowercaseTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(DEFAULT_6BIT_CHARSET_LOWERCASE, true, true);
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 1000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_6BIT_CHARSET_LOWERCASE);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void edgeCasesTest() {
		AsciiCompressor compressor1 = new SixBitAsciiCompressor(true, false);
		assertEquals(0, compressor1.compress(new byte[0]).length);
		assertEquals(0, compressor1.compress(new byte[]{}).length);
		assertEquals(0, compressor1.compress(getBytes("")).length);
		assertEquals(0, compressor1.compress("".getBytes(US_ASCII)).length);
		assertEquals(0, compressor1.compress("").length);
		AsciiCompressor compressor2 = new SixBitAsciiCompressor(true, true);
		assertEquals(0, compressor2.compress(new byte[0]).length);
		assertEquals(0, compressor2.compress(new byte[]{}).length);
		assertEquals(0, compressor2.compress(getBytes("")).length);
		assertEquals(0, compressor2.compress("".getBytes(US_ASCII)).length);
		assertEquals(0, compressor2.compress("").length);
		String nullStr = null;
		assertThrows(NullPointerException.class, () -> compressor1.compress(nullStr));
		assertThrows(NullPointerException.class, () -> compressor2.compress(nullStr));
	}

	@Test
	void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'A', (byte) 'Ç', 'B', 'C'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("AGBC", new String(decompressed, US_ASCII));
	}

	@Test
	void ignoreInvalidCharsTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor();
		for (int i = 0; i < 3000; i++)
			for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
				byte[] input = new byte[]{'A', (byte) asciiCode, 'B', 'C', 'D', (byte) 'Ç'};
				byte[] compressed = compressor.compress(input);
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(input.length, decompressed.length);
			}
	}

	@Test
	void compressionRateTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		final int hundredMb = 100 * 1024 * 1024;
		final byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) 'A');
		final byte[] compressed = compressor.compress(input);
		assertEquals(75, compressed.length / 1024 / 1024); // 25% compression rate.
	}

}
