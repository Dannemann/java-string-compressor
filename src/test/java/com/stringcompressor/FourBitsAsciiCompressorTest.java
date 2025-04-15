package com.stringcompressor;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FourBitsAsciiCompressorTest {

	@Test
	public void compressDecompressTest() {
		String str = createRandomString(100);
		AsciiCompressor compressor = new FourBitsAsciiCompressor();
		long strSizeBefore = GraphLayout.parseInstance(str).totalSize();
		byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
		long strSizeAfter = GraphLayout.parseInstance(compressed).totalSize();
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(str, new String(decompressed, US_ASCII));
//		assertEquals(strSizeAfter, strSizeBefore / 2);
	}

	/**
	 * @param lenMb The String size in megabytes.
	 * @return A String with length of {@code len} megabytes.
	 */
	String createRandomString(int lenMb) {
		var mbLen = lenMb * 1024 * 1024;
		StringBuilder sb = new StringBuilder(mbLen);

		for (var i = 0; i < mbLen; i++)
			sb.append((char) ('0' + (i % 10)));

		return sb.toString();
	}

}
