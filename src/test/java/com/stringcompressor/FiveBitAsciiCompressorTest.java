package com.stringcompressor;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressorTest {

	@Test
	public void test() throws InterruptedException {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);

		for (int j = 0; j <= 500; j++)
			for (int i = 0; i <= 5000; i++) {
				String str = createRandomString(j);
	//			System.out.println(" ### TESTING FOR: " + str);
	//			String str = "00";
	//			String str = "193;";
	//			String str = "+908;0+3";
				byte[] compressed = compressor.compress(str.getBytes());
				byte[] decompressed = compressor.decompress(compressed);
				assertEquals(str, new String(decompressed, US_ASCII));

	//			Thread.sleep(10);
			}
	}

	private String createRandomString(int length) {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append((char) (DEFAULT_4BIT_CHARSET[rand.nextInt(DEFAULT_4BIT_CHARSET.length)]));
		return sb.toString();
	}



	@Test
	public void even() {
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
	public void odd() {
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);

		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210,".getBytes(US_ASCII));
		assertEquals("[0, 68, 50, 20, -57, 66, 84, -74, 53, -49, 123, -102, -59, -87, 40, 57, -118, 65, -120, 32, 120, 0]", Arrays.toString(compressed));
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals("0123456789;#-+.,,.+-#;9876543210,", new String(decompressed, US_ASCII));
	}

}
