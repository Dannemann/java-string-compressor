package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
class FourBitAsciiCompressorTest extends BaseTest {

	@Test
	void usageExample() {
		// A string to be compressed. Whenever possible, prefer working directly with byte[] to avoid creating String objects.
		byte[] inputStr = "0123456789".getBytes(US_ASCII);

		// Creates a compressor with the default supported character set (see FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET).
		AsciiCompressor compressor = new FourBitAsciiCompressor();

		// Creates a compressor with a custom charset, input validation, and input source preservation.
		// Throws an exception when invalid characters are present; useful for debugging purposes
		// (invalid characters should be silently ignored in production). Default is false.
		// By default, the compressor overwrites the original input to minimize memory usage (useful for big strings).
		// Set to true to prevent this. Default is false.
//		AsciiCompressor customCompressor = new FourBitAsciiCompressor(new byte[]{/* custom charset */}, true, true);

		byte[] compressed = compressor.compress(inputStr);
		byte[] decompressed = compressor.decompress(compressed);

		assertEquals("0123456789", new String(decompressed, US_ASCII));

		// If preserveOriginal is false, this will fail because inputStr has been modified.
//		assertArrayEquals(inputStr, decompressed);
	}

	@Test
	void validCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		new FourBitAsciiCompressor(customCharset);
	}

	@Test
	void excessCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 17.", e.getMessage());
	}

	@Test
	void missingCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 15.", e.getMessage());
	}

	@Test
	void invalidCustomCharsetTest() {
		byte[] customCharset = new byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
	}

	@Test
	void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"Ç\"", e.getMessage());
	}

	@Test
	void invalidCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'Z'}));
		assertEquals("Character 'Z' (code point 90) is not defined in the supported characters array. String: \"Z\"", e.getMessage());
	}

	@Test
	void compressDecompressSmallStringTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 500000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_4BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void compressDecompressBigStringTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 10000; i++) {
				final byte[] str = generateRandomStringBytes(length, DEFAULT_4BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	void edgeCasesTest() {
		AsciiCompressor compressor1 = new FourBitAsciiCompressor(true, false);
		assertEquals(0, compressor1.compress(new byte[0]).length);
		assertEquals(0, compressor1.compress(new byte[]{}).length);
		assertEquals(0, compressor1.compress(getBytes("")).length);
		assertEquals(0, compressor1.compress("".getBytes(US_ASCII)).length);
		assertEquals(0, compressor1.compress("").length);
		AsciiCompressor compressor2 = new FourBitAsciiCompressor(true, true);
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
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'0', (byte) 'Ç', '2', '3'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(";;23", new String(decompressed, US_ASCII));
	}

	@Test
	void ignoreInvalidCharsTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		for (int i = 0; i < 3000; i++)
			for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
				byte[] input = new byte[]{'0', (byte) asciiCode, '2', '3', '4', (byte) 'Ç'};
				byte[] compressed = compressor.compress(input);
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(input.length, decompressed.length);
			}
	}

	@Test
	void compressionRateTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		final int hundredMb = 100 * 1024 * 1024;
		final byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) '0');
		final byte[] compressed = compressor.compress(input);
		assertEquals(50, compressed.length / 1024 / 1024); // 50% compression rate.
	}

}
