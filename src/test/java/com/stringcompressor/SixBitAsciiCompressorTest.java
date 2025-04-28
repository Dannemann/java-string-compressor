package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class SixBitAsciiCompressorTest extends BaseTest {

	@Test
	public void validCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62, 63};
		new SixBitAsciiCompressor(customSupportedCharset);
	}

	@Test
	public void excessCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62, 63, 64};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customSupportedCharset));
		assertEquals("6-bit compressor requires a set of exactly 64 characters. Currently 65.", e.getMessage());
	}

	@Test
	public void missingCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customSupportedCharset));
		assertEquals("6-bit compressor requires a set of exactly 64 characters. Currently 63.", e.getMessage());
	}

	@Test
	public void invalidCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			-1, 0, 1, 2, 3, 4, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 56, 57, 58, 59, 60, 61, 62};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new SixBitAsciiCompressor(customSupportedCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
	}

	@Test
	public void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"�\"", e.getMessage());
	}

	@Test
	public void invalidCharCompressTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'|'}));
		assertEquals("Character '|' (code point 124) is not defined in the supported characters array. String: \"|\"", e.getMessage());
	}

	@Test
	public void compressDecompressSmallStringTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		compressor.preserveOriginal = true;
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 500000; i++) {
				final byte[] str = generateRandomString(length, DEFAULT_6BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	public void compressDecompressBigStringTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		compressor.preserveOriginal = true;
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 10000; i++) {
				final byte[] str = generateRandomString(length, DEFAULT_6BIT_CHARSET);
				final byte[] compressed = compressor.compress(str);
				final byte[] decompressed = compressor.decompress(compressed);
				assertArrayEquals(str, decompressed);
			}
	}

	@Test
	public void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new SixBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'A', (byte) 'Ç', 'B', 'C'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("AGBC", new String(decompressed, US_ASCII));
	}

	@Test
	public void ignoreInvalidCharsTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor();
		for (int i = 0; i < 3000; i++)
			for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
				final byte[] input = new byte[]{'A', (byte) asciiCode, 'B', 'C', 'D', (byte) 'Ç'};
				final byte[] compressed = compressor.compress(input);
				final byte[] decompressed = compressor.decompress(compressed);
				assertEquals(input.length, decompressed.length);
			}
	}

	@Test
	public void compressionRateTest() {
		final AsciiCompressor compressor = new SixBitAsciiCompressor(true);
		final int hundredMb = 100 * 1024 * 1024;
		final byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) 'A');
		final byte[] compressed = compressor.compress(input);
		Assertions.assertEquals(75, compressed.length / 1024 / 1024); // 25% compression rate.
	}

}
