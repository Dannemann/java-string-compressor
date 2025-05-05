package com.dannemann.stringcompressor;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jean Dannemann Carone
 */
class FiveBitBinarySearchTest extends BaseTest {

	@RepeatedTest(50)
	void searchSmallStringTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(2_000_000, 0, 100, DEFAULT_5BIT_CHARSET);
		final byte[][] destiny = new byte[source.size()][];
		final AsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
		final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
		managed.compressAll(source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destiny, getBytes(source.get(i))));
	}

	@RepeatedTest(50)
	void searchBigStringTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(50_000, 4500, 5000, DEFAULT_5BIT_CHARSET);
		final byte[][] destiny = new byte[source.size()][];
		final AsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
		final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
		managed.compressAll(source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destiny, getBytes(source.get(i))));
	}

	static String[] words = {"", "A", "ABA", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON", "HYPNOSIS", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};

	@Test
	public void testEdgeCases() {
		List<String> source = List.of(words);
		final byte[][] destiny = new byte[source.size()][];
		FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
		final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
		managed.compressAll(source);

		int r = FiveBitBinarySearch.search(destiny, "A");
		System.out.println(r);


//		List<String> mass = List.of("");
//		byte[][] mass = new byte[1][];
//		byte[][] compressedMass = compressor.compress(mass);
//
//		int bsRes = binarySearch(compressedMass, "");
//		assertEquals(0, bsRes);

//		byte[][] mass = new byte[][]{};
//		int a = binarySearch(mass, getBytes(""));
//		assertEquals(-1, a);
	}

//	@Test
//	public void testBinarySearch2() {
//		byte[][] stringMass = compressor.compress(List.of("A LONG STRING FOR THE EXECUTION OF THIS TEST."));
//		byte[] key = getBytes("A LONG STRING");
//
//		int result = binarySearch(stringMass, key);
//		System.out.println(result);
//	}

}
