package com.dannemann.stringcompressor;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
class FiveBitBinarySearchTest extends BaseTest {

	private static final AsciiCompressor COMPRESSOR = new FiveBitAsciiCompressor(true, true);

	@RepeatedTest(50)
	void searchSmallStringTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(2_000_000, 0, 100, DEFAULT_5BIT_CHARSET);
		final byte[][] destiny = new byte[source.size()][];
		final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(COMPRESSOR, destiny);
		managed.compressAndAddAll(source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destiny, getBytes(source.get(i))));
	}

	@RepeatedTest(50)
	void searchBigStringTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(50_000, 4500, 5000, DEFAULT_5BIT_CHARSET);
		final byte[][] destiny = new byte[source.size()][];
		final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(COMPRESSOR, destiny);
		managed.compressAndAddAll(source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destiny, getBytes(source.get(i))));
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	// Edge cases:

	// TODO: Also test symbols (. , @) at the beginning and end.
	String[] emptyWordArray = {""};
	String[] wordArray = {"WORD"};
	String[] wordsArray = {
		"", "A", "ABA", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON",
		"HYPNOSIS", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};
	List<String> wordList = new ArrayList<>(Arrays.asList(wordArray));
	List<String> wordsList = new ArrayList<>(Arrays.asList(wordsArray));
	byte[][] compressedWord = new byte[wordList.size()][];
	byte[][] compressedWords = new byte[wordsList.size()][];
	final String nullRef = null;
	final String key = "A";

	{
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, compressedWord, wordList);
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, compressedWords, wordsList);
	}
	
	@Test
	public void nullAndEmptySearchEdgeCaseTest() {
		assertThrows(NullPointerException.class, () -> FiveBitBinarySearch.search(null, key));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(null, key));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], nullRef));
		assertEquals(-1, Arrays.binarySearch(new String[0], null));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], ""));
		assertEquals(-1, Arrays.binarySearch(new String[0], ""));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], key));
		assertEquals(-1, Arrays.binarySearch(new String[0], key));
		assertThrows(NullPointerException.class, () -> FiveBitBinarySearch.search(compressedWord, nullRef));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(wordArray, null));
		assertEquals(-1, FiveBitBinarySearch.search(compressedWord, ""));
		assertEquals(-1, Arrays.binarySearch(wordArray, ""));
		assertEquals(0, FiveBitBinarySearch.search(compressedWords, ""));
		assertEquals(0, Arrays.binarySearch(wordsArray, ""));
	}
	
	@Test
	public void edgeCasesTest() {


		assertEquals(-1, FiveBitBinarySearch.search(compressedWord, key));
		assertEquals(-1, Arrays.binarySearch(wordArray, key));
		
		
		
		assertEquals(0, FiveBitBinarySearch.search(compressedWord, "WORD"));
		assertEquals(0, Arrays.binarySearch(wordArray, "WORD"));
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
