package com.stringcompressor;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FourBitsCompressorTest {

	@Test
	public void compressDecompressTest() {
		String str = createRandomString(100);
		Compressor compressor = new FourBitsCompressor();
		long strSizeBefore = GraphLayout.parseInstance(str).totalSize();
		byte[] compressed = compressor.compress(str.getBytes(UTF_8));
		long strSizeAfter = GraphLayout.parseInstance(compressed).totalSize();
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(new String(compressed, UTF_8), new String(decompressed, UTF_8));
//		assertEquals(strSizeAfter, strSizeBefore / 2);
	}

	/**
	 * @param lenMb The string size in megabytes.
	 * @return A string with length of {@code len} megabytes.
	 */
	String createRandomString(int lenMb) {
		var mbLen = lenMb * 1024 * 1024;
		StringBuilder sb = new StringBuilder(mbLen);

		for (var i = 0; i < mbLen; i++)
			sb.append((char) ('0' + (i % 10)));

		return sb.toString();
	}

}
