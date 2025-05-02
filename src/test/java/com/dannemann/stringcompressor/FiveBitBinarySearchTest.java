package com.dannemann.stringcompressor;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class FiveBitBinarySearchTest extends BaseTest {

	public static void main(String[] args) {
		System.out.println("Initializing mass.");
		List<String> mass = generateOrderedStringMass(5_000_000, 10, 100);

		System.out.println("Compressing mass:");
		FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor();
		byte[][] compressedMass = compressor.compress(mass);

		System.out.println("Binary search.");

		int massLen = mass.size();
		for (int i = 0; i < massLen; i++) {
			int bsRes = FiveBitBinarySearch.binarySearch(compressedMass, mass.get(i).getBytes(US_ASCII));
			Assertions.assertEquals(i, bsRes);
		}
	}


	// TODO: Can have way better performance.
	static List<String> generateOrderedStringMass(final int quantity, final int minStrSize, final int maxStrSize) {
		Set<String> massSet = new HashSet<>((int) Math.ceil(quantity / .75));
		final Random random = new Random();

		for (int i = 0; i < quantity; i++) {
			final int strSize = random.nextInt(5, 10);
			final byte[] randomStrBytes = generateRandomString(strSize, DEFAULT_5BIT_CHARSET);
			massSet.add(new String(randomStrBytes, US_ASCII));
		}

		final List<String> massList = new ArrayList<>(massSet);
		massSet = null;

		Collections.sort(massList);

		return massList;
	}

}
