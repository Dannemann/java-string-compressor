package com.stringcompressor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

import java.nio.charset.StandardCharsets;

public class FourBitsCompressorTest {

	@Test
	public void test() {


		String str = createRandomString(100);

		long before = GraphLayout.parseInstance(str).totalSize();

		System.out.println();

		byte[] compressedBytes = new FourBitsCompressor().compress(str.getBytes(StandardCharsets.UTF_8));

		long after = GraphLayout.parseInstance(compressedBytes).totalSize();

		System.out.println(after / 1024.0 / 1024.0);


		Assertions.assertEquals(50, (int) (after / 1024.0 / 1024.0));
	}

	/**
	 *
	 * @param len The string size in megabytes.
	 * @return A string with length of {@code len} megabytes.
	 */
	String createRandomString(int len) {
		var mbLen = len * 1024 * 1024;
		StringBuilder sb = new StringBuilder(mbLen);

		for (var i = 0; i < mbLen; i++)
			sb.append((char) ('0' + (i % 10)));

		return sb.toString();
	}

}
