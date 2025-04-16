package com.stringcompressor;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FourBitsAsciiCompressorTest {

	@Test
	public void compressDecompressTest() {
		AsciiCompressor compressor = new FourBitsAsciiCompressor();
		compressor.setThrowException(true);
		String str = createRandomString(100, compressor.supportedCharset);
		long strSizeBefore = GraphLayout.parseInstance(str).totalSize() / 1024 / 1024;
		byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
		long strSizeAfter = GraphLayout.parseInstance(compressed).totalSize() / 1024 / 1024;
		byte[] decompressed = compressor.decompress(compressed);
		assertEquals(str, new String(decompressed, US_ASCII));
		assertEquals(strSizeAfter, strSizeBefore / 2);
	}

	private String createRandomString(int lenMb, byte[] supportedCharset) {
		int mbLen = lenMb * 1024 * 1024;
		StringBuilder sb = new StringBuilder(mbLen);

		for (int i = 0; i < mbLen; i++)
			sb.append((char) (supportedCharset[i % supportedCharset.length]));

		return sb.toString();
	}

}
