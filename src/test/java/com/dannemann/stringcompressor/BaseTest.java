package com.dannemann.stringcompressor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.US_ASCII;

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
		Arrays.parallelSetAll(strings, i -> new String(generateRandomStringBytes(ThreadLocalRandom.current().nextInt(minStrSize, maxStrSize), charset), US_ASCII));
		return strings;
	}

	protected static List<String> generateRandomStringList(final int numElements, final int minStrSize, final int maxStrSize, byte[] charset) {
		final List<String> strings = new ArrayList<>(numElements);
		for (int i = 0; i < numElements; i++)
			strings.add(new String(generateRandomStringBytes(RANDOM.nextInt(minStrSize, maxStrSize), charset), US_ASCII));
		return strings;
	}

//	// TODO: Can have way better performance.
//	public static List<String> generateUniqueOrderedStringMass(final int quantity, final int minStrSize, final int maxStrSize) {
//		Set<String> massSet = new HashSet<>((int) Math.ceil(quantity / .75));
//
//		for (int i = 0; i < quantity; i++) {
//			final int strSize = RANDOM.nextInt(minStrSize, maxStrSize);
//			final byte[] randomStrBytes = generateRandomStringBytes(strSize, DEFAULT_5BIT_CHARSET); // TODO: Charset hardcoded
//			massSet.add(new String(randomStrBytes, US_ASCII));
//		}
//
//		final List<String> massList = new ArrayList<>(massSet);
//		massSet = null;
//
//		Collections.sort(massList);
//
//		return massList;
//	}

}
