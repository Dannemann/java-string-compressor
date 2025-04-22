package com.stringcompressor;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressorTest {

	@Test
	public void even() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);

		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210".getBytes(US_ASCII));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32]", Arrays.toString(compressed));
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]", Arrays.toString(decompressed));
	}


	@Test
	public void odd() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);

		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210,".getBytes(US_ASCII));
		System.out.println(Arrays.toString(compressed));
//		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32]", Arrays.toString(compressed));

		byte[] decompressed = compressor.decompress(compressed);
		System.out.println(Arrays.toString(decompressed));
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 15]", Arrays.toString(decompressed));
	}

}
