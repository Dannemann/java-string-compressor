package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FourBitAsciiCompressorTest {

	@Test
	public void compressDecompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);

		String strEven = new String(DEFAULT_4BIT_CHARSET, US_ASCII);
		byte[] compressedEven = compressor.compress(strEven.getBytes(US_ASCII));
		assertEquals(strEven.length() / 2 + (strEven.length() % 2 != 0 ? 2 : 1), compressedEven.length);
		byte[] decompressedEven = compressor.decompress(compressedEven);
		assertEquals(strEven.length(), decompressedEven.length);
		assertEquals(strEven, new String(decompressedEven, US_ASCII));

		byte[] strOddBytes = Arrays.copyOf(DEFAULT_4BIT_CHARSET, DEFAULT_4BIT_CHARSET.length + 1);
		strOddBytes[strOddBytes.length - 1] = DEFAULT_4BIT_CHARSET[0];
		String strOdd = new String(strOddBytes, US_ASCII);
		byte[] compressedOdd = compressor.compress(strOdd.getBytes(US_ASCII));
		assertEquals(strOdd.length() / 2 + (strOdd.length() % 2 != 0 ? 2 : 1), compressedOdd.length);
		byte[] decompressedOdd = compressor.decompress(compressedOdd);
		assertEquals(strOdd.length(), decompressedOdd.length);
		assertEquals(strOdd, new String(decompressedOdd, US_ASCII));
	}

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
		assertEquals("4-bit compressor supports a minimum of 1 and a maximum of 16 different characters. Currently 17.", e.getMessage());
	}

	@Test
	public void invalidCustomCharsetTest() {
		byte[] customSupportedCharset = new byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
		assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code -1)", e.getMessage());
	}

	@Test
	public void notAsciiCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
		assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code -57) in \"�\"", e.getMessage());
	}

	@Test
	public void invalidCharCompressTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor(true);
		CharacterNotSupportedException e = assertThrows(
			CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'Z'}));
		assertEquals("Character 'Z' (code 90) is not defined in the supported characters array. String: \"Z\"", e.getMessage());
	}

	@Test
	public void ignoreInvalidCharTest() {
		AsciiCompressor compressor = new FourBitAsciiCompressor();
		byte[] compressed = compressor.compress(new byte[]{(byte) '0', 'A', '2', '3'}); // TODO: BUGGED WITH ODD LENGTH.
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(",,23", new String(decompressed, US_ASCII));
	}

	// Utils:

	private String createRandomString(int oddOrEvenLength) {
		int bytes = 10 + oddOrEvenLength;
		StringBuilder sb = new StringBuilder(bytes);
		for (int i = 0; i < bytes; i++)
			sb.append((char) (DEFAULT_4BIT_CHARSET[i % DEFAULT_4BIT_CHARSET.length]));
		return sb.toString();
	}

}
