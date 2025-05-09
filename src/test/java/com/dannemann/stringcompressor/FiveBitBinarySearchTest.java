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

	private static final String[] EMPTY_WORD_ARRAY = {""};
	private static final String[] WORD_ARRAY = {"WORD"};
	private static final String[] TWO_WORDS_ARRAY = {"AA", "CC"};
	private static final String[] WORDS_ARRAY = {
		"", "A", "ABA", "ABA", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON",
		"HYPNOSIS", "IA", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};
	private static final String[] SPECIAL_ARRAY = {" ", "@", "ALSO", "Z"};
	private static final List<String> EMPTY_WORDS_LIST = new ArrayList<>(Arrays.asList(EMPTY_WORD_ARRAY));
	private static final List<String> WORD_LIST = new ArrayList<>(Arrays.asList(WORD_ARRAY));
	private static final List<String> TWO_WORDS_LIST = new ArrayList<>(Arrays.asList(TWO_WORDS_ARRAY));
	private static final List<String> WORDS_LIST = new ArrayList<>(Arrays.asList(WORDS_ARRAY));
	private static final List<String> SPECIAL_LIST = new ArrayList<>(Arrays.asList(SPECIAL_ARRAY));
	private static final byte[][] COMPRESSED_EMPTY_WORD = new byte[EMPTY_WORDS_LIST.size()][];
	private static final byte[][] COMPRESSED_WORD = new byte[WORD_LIST.size()][];
	private static final byte[][] COMPRESSED_TWO_WORDS = new byte[TWO_WORDS_LIST.size()][];
	private static final byte[][] COMPRESSED_WORDS = new byte[WORDS_LIST.size()][];
	private static final byte[][] COMPRESSED_SPECIAL = new byte[SPECIAL_LIST.size()][];
	private static final String NULL_REF = null;
	private static final AsciiCompressor COMPRESSOR = new FiveBitAsciiCompressor(true, true);

	static {
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_EMPTY_WORD, EMPTY_WORDS_LIST);
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_WORD, WORD_LIST);
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_TWO_WORDS, TWO_WORDS_LIST);
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_WORDS, WORDS_LIST);
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_SPECIAL, SPECIAL_LIST);
	}

	@Test
	public void nullAndEmptySearchEdgeCaseTest() {
		assertThrows(NullPointerException.class, () -> FiveBitBinarySearch.search(null, "A"));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(null, "A"));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], NULL_REF));
		assertEquals(-1, Arrays.binarySearch(new String[0], null));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], ""));
		assertEquals(-1, Arrays.binarySearch(new String[0], ""));
		assertEquals(-1, FiveBitBinarySearch.search(new byte[0][], "A"));
		assertEquals(-1, Arrays.binarySearch(new String[0], "A"));
		assertThrows(NullPointerException.class, () -> FiveBitBinarySearch.search(COMPRESSED_WORD, NULL_REF));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(WORD_ARRAY, null));
		assertEquals(0, FiveBitBinarySearch.search(COMPRESSED_EMPTY_WORD, ""));
		assertEquals(0, Arrays.binarySearch(EMPTY_WORD_ARRAY, ""));
		assertEquals(-1, FiveBitBinarySearch.search(COMPRESSED_WORD, ""));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, ""));
		assertEquals(0, FiveBitBinarySearch.search(COMPRESSED_WORDS, ""));
		assertEquals(0, Arrays.binarySearch(WORDS_ARRAY, ""));
	}

	@Test
	public void characterSearchEdgeCaseTest() {
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_EMPTY_WORD, "A"));
		assertEquals(-2, Arrays.binarySearch(EMPTY_WORD_ARRAY, "A"));
		assertEquals(-1, FiveBitBinarySearch.search(COMPRESSED_WORD, "A"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "A"));
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_WORD, "X"));
		assertEquals(-2, Arrays.binarySearch(WORD_ARRAY, "X"));
		assertEquals(-1, FiveBitBinarySearch.search(COMPRESSED_WORD, "A"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "A"));
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_TWO_WORDS, "B"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "B"));
		assertEquals(-1, FiveBitBinarySearch.search(COMPRESSED_TWO_WORDS, "A"));
		assertEquals(-1, Arrays.binarySearch(TWO_WORDS_ARRAY, "A"));
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_WORDS, " "));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, " "));
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_WORDS, "'"));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, "'"));
		assertEquals(1, FiveBitBinarySearch.search(COMPRESSED_WORDS, "A"));
		assertEquals(1, Arrays.binarySearch(WORDS_ARRAY, "A"));
		assertEquals(1, FiveBitBinarySearch.search(COMPRESSED_WORDS, "A"));
		assertEquals(1, Arrays.binarySearch(WORDS_ARRAY, "A"));
	}

	@Test
	public void wordSearchEdgeCaseTest() {
		assertEquals(0, FiveBitBinarySearch.search(COMPRESSED_WORD, "WORD"));
		assertEquals(0, Arrays.binarySearch(WORD_ARRAY, "WORD"));
		assertEquals(-2, FiveBitBinarySearch.search(COMPRESSED_TWO_WORDS, "AAB"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "AAB"));
		assertEquals(-3, FiveBitBinarySearch.search(COMPRESSED_WORDS, "AA"));
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "AA"));
		assertEquals(-3, FiveBitBinarySearch.search(COMPRESSED_WORDS, "AB"));
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "AB"));
		assertEquals(3, FiveBitBinarySearch.search(COMPRESSED_WORDS, "ABA"));
		assertEquals(3, Arrays.binarySearch(WORDS_ARRAY, "ABA"));
		assertEquals(-18, FiveBitBinarySearch.search(COMPRESSED_WORDS, "HASH"));
		assertEquals(-18, Arrays.binarySearch(WORDS_ARRAY, "HASH"));
		assertEquals(-19, FiveBitBinarySearch.search(COMPRESSED_WORDS, "HUP"));
		assertEquals(-19, Arrays.binarySearch(WORDS_ARRAY, "HUP"));
		assertEquals(-33, FiveBitBinarySearch.search(COMPRESSED_WORDS, "UMBRELL"));
		assertEquals(-33, Arrays.binarySearch(WORDS_ARRAY, "UMBRELL"));
		assertEquals(32, FiveBitBinarySearch.search(COMPRESSED_WORDS, "UMBRELLA"));
		assertEquals(32, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLA"));
		assertEquals(-34, FiveBitBinarySearch.search(COMPRESSED_WORDS, "UMBRELLAA"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLAA"));
		assertEquals(-34, FiveBitBinarySearch.search(COMPRESSED_WORDS, "ZOP"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "ZOP"));
	}

	@Test
	public void specialChararactersSearch() {
		assertEquals(-1, FiveBitBinarySearch.search(COMPRESSED_SPECIAL, ""));
		assertEquals(-1, Arrays.binarySearch(SPECIAL_ARRAY, ""));
		assertEquals(0, FiveBitBinarySearch.search(COMPRESSED_SPECIAL, " "));
		assertEquals(0, Arrays.binarySearch(SPECIAL_ARRAY, " "));
		assertEquals(1, FiveBitBinarySearch.search(COMPRESSED_SPECIAL, "@"));
		assertEquals(1, Arrays.binarySearch(SPECIAL_ARRAY, "@"));
		assertEquals(3, FiveBitBinarySearch.search(COMPRESSED_SPECIAL, "Z"));
		assertEquals(3, Arrays.binarySearch(SPECIAL_ARRAY, "Z"));
	}

	@Test
	void searchSmallStringsTest() {
		for (int length = 0; length <= 50; length++)
			for (int i = 0; i <= 25_000; i++) {
				final List<String> source = generateRandomUniqueOrderedStringList(500, length, length + 1, DEFAULT_5BIT_CHARSET);
				final byte[][] destination = new byte[source.size()][];
				ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, destination, source);
				for (int j = 0, massLen = source.size(); j < massLen; j++)
					assertEquals(j, FiveBitBinarySearch.search(destination, getBytes(source.get(j))));
			}
	}

	@RepeatedTest(50)
	void searchBigArrayTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(2_000_000, 0, 100, DEFAULT_5BIT_CHARSET);
		final byte[][] destination = new byte[source.size()][];
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destination, getBytes(source.get(i))));
	}

	@RepeatedTest(50)
	void searchBigStringsTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(50_000, 4500, 5000, DEFAULT_5BIT_CHARSET);
		final byte[][] destination = new byte[source.size()][];
		ManagedBulkAsciiCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, FiveBitBinarySearch.search(destination, getBytes(source.get(i))));
	}

}
