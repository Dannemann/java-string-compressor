package com.stringcompressor;

import java.util.Random;

public abstract class BaseTest {

	private static final Random RANDOM = new Random();

	protected static String createRandomString(int length, byte[] charset) {
		StringBuilder sb = new StringBuilder(length);
		int charsetLen = charset.length;
		for (int i = 0; i < length; i++)
			sb.append((char) (charset[RANDOM.nextInt(charsetLen)]));
		return sb.toString();
	}

}
