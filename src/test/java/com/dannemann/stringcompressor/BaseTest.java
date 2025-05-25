package com.dannemann.stringcompressor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.dannemann.stringcompressor.AsciiCompressor.getString;

/**
 * @author Jean Dannemann Carone
 */
abstract class BaseTest {

	protected static final Random RANDOM = new Random();

	protected static byte[] generateRandomStringBytes(final int length, final byte[] charset) {
		final byte[] string = new byte[length];
		final int charSetLen = charset.length;
		for (int i = 0; i < length; i++)
			string[i] = charset[ThreadLocalRandom.current().nextInt(charSetLen)];
		return string;
	}

	protected static byte[][] generateRandomByteArray(final int numElements, final int minStrSize, final int maxStrSize, byte[] charset) {
		final byte[][] strings = new byte[numElements][];
		Arrays.parallelSetAll(strings, i -> generateRandomStringBytes(ThreadLocalRandom.current().nextInt(minStrSize, maxStrSize), charset));
		return strings;
	}

	protected static String[] generateRandomStringArray(final int numElements, final int minStrSize, final int maxStrSize, byte[] charset) {
		final String[] strings = new String[numElements];
		Arrays.parallelSetAll(strings, i -> getString(generateRandomStringBytes(ThreadLocalRandom.current().nextInt(minStrSize, maxStrSize), charset)));
		return strings;
	}

	protected static List<String> generateRandomStringList(final int numElements, final int minStrSize, final int maxStrSize, byte[] charset) {
		final List<String> strings = new ArrayList<>(numElements);
		for (int i = 0; i < numElements; i++)
			strings.add(getString(generateRandomStringBytes(RANDOM.nextInt(minStrSize, maxStrSize), charset)));
		return strings;
	}

	protected List<String> generateRandomUniqueOrderedStringList(final int quantity, final int minStrSize, final int maxStrSize, final byte[] charset) {
		Set<String> stringSet = new HashSet<>((int) Math.ceil(quantity / .75));
		for (int i = 0; i < quantity; i++)
			stringSet.add(getString(generateRandomStringBytes(RANDOM.nextInt(minStrSize, maxStrSize), charset)));
		final List<String> stringList = new ArrayList<>(stringSet);
		stringSet = null;
		Collections.sort(stringList);
		return stringList;
	}

}
