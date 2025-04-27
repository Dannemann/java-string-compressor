package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressorTest extends BaseTest {

	@Test
	public void validCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
		new FiveBitAsciiCompressor(customSupportedCharset);
	}

	@Test
	public void excessCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FiveBitAsciiCompressor(customSupportedCharset));
		assertEquals("5-bit compressor requires a set of exactly 32 characters. Currently 33.", e.getMessage());
	}

	@Test
	public void missingCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FiveBitAsciiCompressor(customSupportedCharset));
		assertEquals("5-bit compressor requires a set of exactly 32 characters. Currently 31.", e.getMessage());
	}

	@Test
	public void invalidCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{
			-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FiveBitAsciiCompressor(customSupportedCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
	}

	@Test
	public void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"�\"", e.getMessage());
	}

	@Test
	public void invalidCharCompressTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'9'}));
		assertEquals("Character '9' (code point 57) is not defined in the supported characters array. String: \"9\"", e.getMessage());
	}

	@Test
	public void compressDecompressTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		for (int length = 0; length <= 200; length++)
			for (int i = 0; i <= 100000; i++) {
				String str = createRandomString(length, DEFAULT_5BIT_CHARSET);
				byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(str, new String(decompressed, US_ASCII));
			}
	}

	@Test
	public void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'A', (byte) 'Ç', 'B', 'C'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("AGBC", new String(decompressed, US_ASCII));
	}

	@Test
	public void ignoreInvalidCharsTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		for (int i = 0; i < 3000; i++)
			for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
				byte[] input = new byte[]{'A', (byte) asciiCode, 'B', 'C', 'D', (byte) 'Ç'};
				byte[] compressed = compressor.compress(input);
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(input.length, decompressed.length);
			}
	}

	@Test
	public void compressionRateTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		int hundredMb = 100 * 1024 * 1024;
		byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) 'A');
		byte[] compressed = compressor.compress(input);
		Assertions.assertEquals(62, compressed.length / 1024 / 1024); // 38% compression rate.
	}

}
