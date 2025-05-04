//package com.dannemann.stringcompressor.search;
//
//import com.dannemann.stringcompressor.AsciiCompressor;
//import com.dannemann.stringcompressor.BulkAsciiCompressor;
//import com.dannemann.stringcompressor.FiveBitAsciiCompressor;
//import com.dannemann.stringcompressor.BaseTest;
//import org.junit.jupiter.api.RepeatedTest;
//import org.junit.jupiter.api.Test;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
//import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
//import static com.dannemann.stringcompressor.search.FiveBitBinarySearch.binarySearch;
//import static java.nio.charset.StandardCharsets.US_ASCII;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class FiveBitBinarySearchTest extends BaseTest {
//
//
//
//
//	FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor();
//
//	static String[] words = {"", "ABACUS", "ABB", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON", "HYPNOSIS", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};
//
//	//	@RepeatedTest(100)
//	@RepeatedTest(1)
//	public void searchSmallStringTest() {
//		final AsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
//		final BulkAsciiCompressor bulk = new BulkAsciiCompressor(compressor);
//		final List<String> stringMass = generateUniqueOrderedStringMass(2_000_000, 1, 100);
//		final byte[][] compressedMass = new byte[stringMass.size()][];
//
//		bulk.bulkCompress(stringMass, compressedMass, 0);
//
//		for (int i = 0, massLen = stringMass.size(); i < massLen; i++)
//			assertEquals(i, binarySearch(compressedMass, getBytes(stringMass.get(i))));
//	}
//
////	//	@RepeatedTest(100)
////	@RepeatedTest(1)
////	public void searchBigStringTest() {
////		final FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
////		final List<String> stringMass = generateUniqueOrderedStringMass(50_000, 4500, 5000);
////		final byte[][] compressedMass = compressor.compress(stringMass);
////		for (int i = 0, massLen = stringMass.size(); i < massLen; i++)
////			assertEquals(i, binarySearch(compressedMass, getBytes(stringMass.get(i))));
////	}
//
////	@Test
////	public void testEdgeCases() {
////		FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
////
//////		List<String> mass = List.of("");
////		byte[][] mass = new byte[1][];
////		byte[][] compressedMass = compressor.compress(mass);
////
////		int bsRes = binarySearch(compressedMass, "");
////		assertEquals(0, bsRes);
////
////
//////		byte[][] mass = new byte[][]{};
//////		int a = binarySearch(mass, getBytes(""));
//////		assertEquals(-1, a);
////
////
////	}
//
////	@Test
////	public void testBinarySearch2() {
////		byte[][] stringMass = compressor.compress(List.of("A LONG STRING FOR THE EXECUTION OF THIS TEST."));
////		byte[] key = getBytes("A LONG STRING");
////
////		int result = binarySearch(stringMass, key);
////		System.out.println(result);
////
////	}
//
//
////	// TODO: Can have way better performance.
////	static List<String> generateUniqueOrderedStringMass(final int quantity, final int minStrSize, final int maxStrSize) {
////		final Random random = new Random();
////		Set<String> massSet = new HashSet<>((int) Math.ceil(quantity / .75));
////
////		for (int i = 0; i < quantity; i++) {
////			final int strSize = random.nextInt(minStrSize, maxStrSize);
////			final byte[] randomStrBytes = generateRandomStringBytes(strSize, DEFAULT_5BIT_CHARSET);
////			massSet.add(new String(randomStrBytes, US_ASCII));
////		}
////
////		final List<String> massList = new ArrayList<>(massSet);
////		massSet = null;
////
////		Collections.sort(massList);
////
////		return massList;
////	}
//
//}
