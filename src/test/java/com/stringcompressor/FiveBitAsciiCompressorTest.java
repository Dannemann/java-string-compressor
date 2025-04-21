package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

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
		AsciiCompressor compressor = new FiveBitAsciiCompressor(DEFAULT_4BIT_CHARSET);
		byte[] compressed = compressor.compress("0123456789;#-+.,,.+-#;9876543210".getBytes(US_ASCII));

		System.out.println(Arrays.toString(compressed));


		byte[] decompressed = compressor.decompress(compressed);
		System.out.println(Arrays.toString(decompressed));
	}



}
