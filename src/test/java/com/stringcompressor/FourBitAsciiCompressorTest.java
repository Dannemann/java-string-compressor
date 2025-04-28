package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressorTest extends BaseTest {

	@Test
	public void usageExample() {
		// A string to be compressed. Whenever possible, prefer working directly with byte[] to avoid creating String objects.
		byte[] inputStr = "0123456789".getBytes(US_ASCII);

		// Creates a compressor with the default supported character set (see FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET).
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		// Creates a compressor with a custom charset.
//		AsciiCompressor customCharsetCompressor = new FourBitAsciiCompressor(new byte[]{/* custom charset */});

		// Throws an exception when invalid characters are present; useful for debugging purposes.
		// Invalid characters should be silently ignored in production. Default is false.
		compressor.setThrowException(true);
		// Compressor overwrites the original string ("inputStr") to reduce memory usage.
		// Set to true to prevent this. Default is false.
		compressor.setPreserveOriginal(true);

		byte[] compressed = compressor.compress(inputStr);
		byte[] decompressed = compressor.decompress(compressed);

		assertEquals("0123456789", new String(decompressed, US_ASCII));
		assertArrayEquals(inputStr, decompressed); // If preserveOriginal is false, this will fail because inputStr has been modified.
	}

	@Test
	public void validCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		new FourBitAsciiCompressor(customCharset);
	}

	@Test
	public void excessCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 17.", e.getMessage());
	}

	@Test
	public void missingCustomCharsetTest() {
		byte[] customCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 15.", e.getMessage());
	}

	@Test
	public void invalidCustomCharsetTest() {
		byte[] customCharset = new byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
	}

	@Test
	public void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"�\"", e.getMessage());
	}

	@Test
	public void invalidCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'Z'}));
		assertEquals("Character 'Z' (code point 90) is not defined in the supported characters array. String: \"Z\"", e.getMessage());
	}

	@Test
	public void compressDecompressSmallStringTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		compressor.setPreserveOriginal(true);
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 500000; i++) {
				final byte[] str = generateRandomString(length, DEFAULT_4BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	public void compressDecompressBigStringTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		compressor.setPreserveOriginal(true);
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 10000; i++) {
				final byte[] str = generateRandomString(length, DEFAULT_4BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	public void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'0', (byte) 'Ç', '2', '3'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(",,23", new String(decompressed, US_ASCII));
	}

	@Test
	public void ignoreInvalidCharsTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor();
		for (int i = 0; i < 3000; i++)
			for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
				final byte[] input = new byte[]{'0', (byte) asciiCode, '2', '3', '4', (byte) 'Ç'};
				final byte[] compressed = compressor.compress(input);
				final byte[] decompressed = compressor.decompress(compressed);
				assertEquals(input.length, decompressed.length);
			}
	}

	@Test
	public void compressionRateTest() {
		final AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		final int hundredMb = 100 * 1024 * 1024;
		final byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) '0');
		final byte[] compressed = compressor.compress(input);
		Assertions.assertEquals(50, compressed.length / 1024 / 1024); // 50% compression rate.
	}

}
