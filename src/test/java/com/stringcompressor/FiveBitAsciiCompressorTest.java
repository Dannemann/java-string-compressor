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

		byte[] compressed1 = compressor.compress("0123456789;#-+.,,.+-#;9876543210".getBytes(US_ASCII));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32]", Arrays.toString(compressed1));
//		byte[] decompressed1 = compressor.decompress(compressed1);
//		assertEquals("0123456789;#-+.,,.+-#;9876543210", new String(decompressed1, US_ASCII));

		byte[] compressed2 = compressor.compress(",,".getBytes(US_ASCII));
		assertEquals("[123, -64]", Arrays.toString(compressed2));
//		byte[] decompressed2 = compressor.decompress(compressed2);
//		assertEquals(",,", new String(decompressed2, US_ASCII));
	}


	@Test
	public void odd() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);

		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210,".getBytes(US_ASCII));
		System.out.println(Arrays.toString(compressed));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32, 120]", Arrays.toString(compressed));

//		byte[] decompressed = compressor.decompress(compressed);
//		System.out.println(Arrays.toString(decompressed));
//		assertEquals("0123456789;#-+.,,.+-#;9876543210,", new String(decompressed, US_ASCII));
	}

}
