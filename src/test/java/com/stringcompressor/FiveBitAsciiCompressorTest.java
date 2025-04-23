package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static com.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressorTest {

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
		assertEquals("5-bit compressor supports a minimum of 1 and a maximum of 32 different characters. Currently 33.", e.getMessage());
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
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'Z'}));
		assertEquals("Character 'Z' (code point 90) is not defined in the supported characters array. String: \"Z\"", e.getMessage());
	}

	@Test
	public void compressDecompressTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		for (int length = 0; length <= 500; length++)
			for (int i = 0; i <= 3000; i++) {
				String str = createRandomString(length);
				byte[] compressed = compressor.compress(str.getBytes());
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(str, new String(decompressed, US_ASCII));
			}
	}

	@Test
	public void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{'0', (byte) 'Ç', '2', '3'});
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(",,23", new String(decompressed, US_ASCII));
	}

	// Old tests:

	@Test
	public void evenLengthTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);
		byte[] compressed1 = compressor.compress("0123456789;#-+.,,.+-#;9876543210".getBytes(US_ASCII));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32, 0]", Arrays.toString(compressed1));
		byte[] decompressed1 = compressor.decompress(compressed1);
		assertEquals("0123456789;#-+.,,.+-#;9876543210", new String(decompressed1, US_ASCII));
		byte[] compressed2 = compressor.compress(",,".getBytes(US_ASCII));
		assertEquals("[123, -64, 1]", Arrays.toString(compressed2));
		byte[] decompressed2 = compressor.decompress(compressed2);
		assertEquals(",,", new String(decompressed2, US_ASCII));
	}

	@Test
	public void oddLengthTest() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);
		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210,".getBytes(US_ASCII));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32, 120, 0]", Arrays.toString(compressed));
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("0123456789;#-+.,,.+-#;9876543210,", new String(decompressed, US_ASCII));
	}

	// Utils:

	private String createRandomString(int length) {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append((char) (DEFAULT_5BIT_CHARSET[rand.nextInt(DEFAULT_5BIT_CHARSET.length)]));
		return sb.toString();
	}

}
