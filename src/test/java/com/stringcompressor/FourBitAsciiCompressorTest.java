package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressorTest extends BaseTest {

	@Test
	public void validCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		new FourBitAsciiCompressor(customSupportedCharset);
	}

	@Test
	public void excessCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 17.", e.getMessage());
	}

	@Test
	public void missingCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
		assertEquals("4-bit compressor requires a set of exactly 16 characters. Currently 15.", e.getMessage());
	}

	@Test
	public void invalidCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
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
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		for (int length = 0; length <= 100; length++)
			for (int i = 0; i <= 3000000; i++) {
				String str = createRandomString(length, DEFAULT_4BIT_CHARSET);
				byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(str, new String(decompressed, US_ASCII));
			}
	}

	@Test
	public void compressDecompressBigStringTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		for (int length = 2000; length <= 3000; length++)
			for (int i = 0; i <= 1000000; i++) {
				String str = createRandomString(length, DEFAULT_4BIT_CHARSET);
				byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(str, new String(decompressed, US_ASCII));
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
	public void compressionRateTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		int hundredMb = 100 * 1024 * 1024;
		byte[] input = new byte[hundredMb];
		Arrays.fill(input, (byte) '0');
		byte[] compressed = compressor.compress(input);
		Assertions.assertEquals(50, compressed.length / 1024 / 1024); // 50% compression rate.
	}

}
