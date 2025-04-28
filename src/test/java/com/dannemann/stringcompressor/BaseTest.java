package com.dannemann.stringcompressor;

import java.util.Random;

/**
 * @author Jean Dannemann Carone
 */
abstract class BaseTest {

	private static final Random RANDOM = new Random();

	static byte[] generateRandomString(final int length, final byte[] charset) {
		final byte[] string = new byte[length];
		final int charSetLen = charset.length;

		for (int i = 0; i < length; i++)
			string[i] = charset[RANDOM.nextInt(charSetLen)];

		return string;
	}

}
