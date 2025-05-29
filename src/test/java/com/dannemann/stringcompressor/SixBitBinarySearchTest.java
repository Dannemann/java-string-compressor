package com.dannemann.stringcompressor;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
class SixBitBinarySearchTest extends BaseTest {

	// -----------------------------------------------------------------------------------------------------------------
	// Automated search tests:

	private static final AsciiCompressor COMPRESSOR = new SixBitAsciiCompressor(true, true);

	@Test
	void searchSmallStringsTest() {
		for (int length = 0; length <= 50; length++)
			for (int i = 0; i <= 30_000; i++) {
				final List<String> source = generateRandomUniqueOrderedStringList(500, length, length + 1, DEFAULT_6BIT_CHARSET);
				final byte[][] destination = new byte[source.size()][];
				ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
				final SixBitBinarySearch bs = new SixBitBinarySearch(destination, false);
				for (int j = 0, massLen = source.size(); j < massLen; j++)
					assertEquals(j, bs.search(getBytes(source.get(j))));
			}
	}

	@RepeatedTest(100)
	void searchBigArrayTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(2_000_000, 0, 100, DEFAULT_6BIT_CHARSET);
		final byte[][] destination = new byte[source.size()][];
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		final SixBitBinarySearch bs = new SixBitBinarySearch(destination, false);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, bs.search(getBytes(source.get(i))));
	}

	@RepeatedTest(100)
	void searchBigStringsTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(50_000, 4500, 5000, DEFAULT_6BIT_CHARSET);
		final byte[][] destination = new byte[source.size()][];
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		final SixBitBinarySearch bs = new SixBitBinarySearch(destination, false);
		for (int i = 0, massLen = source.size(); i < massLen; i++)
			assertEquals(i, bs.search(getBytes(source.get(i))));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Search edge cases:

	private static final String[] EMPTY_WORD_ARRAY = {""};
	private static final String[] WORD_ARRAY = {"WORD"};
	private static final String[] TWO_WORDS_ARRAY = {"AA", "CC"};
	private static final String[] WORDS_ARRAY = {
		"", "A", "ABA", "ABA", "AMBITION", "ANECDOTE", "B", "BAMBOO", "CANYON", "CARNIVAL", "DANDELION", "DOLPHIN", "ECLECTIC", "ELEPHANT", "FABLE", "GADGET", "GARDEN", "HORIZON",
		"HYPNOSIS", "IA", "ISOTOPE", "JUNGLE", "KALEIDOSCOPE", "LANTERN", "MARATHON", "NEBULA", "OASIS", "PARADOX", "QUARTZ", "RHAPSODY", "SAPPHIRE", "TAPESTRY", "UMBRELLA"};
	private static final String[] SPECIAL_ARRAY = {" ", "@", "ALSO", "}"};
	private static final byte[][] COMPRESSED_EMPTY_WORD = new byte[EMPTY_WORD_ARRAY.length][];
	private static final byte[][] COMPRESSED_WORD = new byte[WORD_ARRAY.length][];
	private static final byte[][] COMPRESSED_TWO_WORDS = new byte[TWO_WORDS_ARRAY.length][];
	private static final byte[][] COMPRESSED_WORDS = new byte[WORDS_ARRAY.length][];
	private static final byte[][] COMPRESSED_SPECIAL = new byte[SPECIAL_ARRAY.length][];
	private static final String NULL_REF = null;

	static {
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_EMPTY_WORD, EMPTY_WORD_ARRAY);
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_WORD, WORD_ARRAY);
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_TWO_WORDS, TWO_WORDS_ARRAY);
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_WORDS, WORDS_ARRAY);
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_SPECIAL, SPECIAL_ARRAY);
	}

	static int sixBitBinarySearch(byte[][] compressedMass, String input) {
		return new SixBitBinarySearch(compressedMass, false).search(getBytes(input));
	}

	static int sixBitPrefixSearch(byte[][] compressedMass, String input) {
		return new SixBitBinarySearch(compressedMass, true).search(getBytes(input));
	}

	@Test
	public void nullAndEmptySearchEdgeCaseTest() {
		assertThrows(NullPointerException.class, () -> sixBitBinarySearch(null, "A"));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(null, "A"));
		assertEquals(-1, sixBitBinarySearch(new byte[0][], NULL_REF));
		assertEquals(-1, Arrays.binarySearch(new String[0], null));
		assertEquals(-1, sixBitBinarySearch(new byte[0][], ""));
		assertEquals(-1, Arrays.binarySearch(new String[0], ""));
		assertEquals(-1, sixBitBinarySearch(new byte[0][], "A"));
		assertEquals(-1, Arrays.binarySearch(new String[0], "A"));
		assertThrows(NullPointerException.class, () -> sixBitBinarySearch(COMPRESSED_WORD, NULL_REF));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(WORD_ARRAY, null));
		assertEquals(0, sixBitBinarySearch(COMPRESSED_EMPTY_WORD, ""));
		assertEquals(0, Arrays.binarySearch(EMPTY_WORD_ARRAY, ""));
		assertEquals(-1, sixBitBinarySearch(COMPRESSED_WORD, ""));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, ""));
		assertEquals(0, sixBitBinarySearch(COMPRESSED_WORDS, ""));
		assertEquals(0, Arrays.binarySearch(WORDS_ARRAY, ""));
	}

	@Test
	public void characterSearchEdgeCaseTest() {
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_EMPTY_WORD, "A"));
		assertEquals(-2, Arrays.binarySearch(EMPTY_WORD_ARRAY, "A"));
		assertEquals(-1, sixBitBinarySearch(COMPRESSED_WORD, "A"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "A"));
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_WORD, "X"));
		assertEquals(-2, Arrays.binarySearch(WORD_ARRAY, "X"));
		assertEquals(-1, sixBitBinarySearch(COMPRESSED_WORD, "A"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "A"));
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_TWO_WORDS, "B"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "B"));
		assertEquals(-1, sixBitBinarySearch(COMPRESSED_TWO_WORDS, "A"));
		assertEquals(-1, Arrays.binarySearch(TWO_WORDS_ARRAY, "A"));
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_WORDS, " "));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, " "));
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_WORDS, "'"));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, "'"));
		assertEquals(1, sixBitBinarySearch(COMPRESSED_WORDS, "A"));
		assertEquals(1, Arrays.binarySearch(WORDS_ARRAY, "A"));
	}

	@Test
	public void wordSearchEdgeCaseTest() {
		assertEquals(0, sixBitBinarySearch(COMPRESSED_WORD, "WORD"));
		assertEquals(0, Arrays.binarySearch(WORD_ARRAY, "WORD"));
		assertEquals(-2, sixBitBinarySearch(COMPRESSED_TWO_WORDS, "AAB"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "AAB"));
		assertEquals(-3, sixBitBinarySearch(COMPRESSED_WORDS, "AA"));
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "AA"));
		assertEquals(-3, sixBitBinarySearch(COMPRESSED_WORDS, "AB"));
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "AB"));
		assertEquals(3, sixBitBinarySearch(COMPRESSED_WORDS, "ABA")); // Duplicate.
		assertEquals(3, Arrays.binarySearch(WORDS_ARRAY, "ABA"));
		assertEquals(-18, sixBitBinarySearch(COMPRESSED_WORDS, "HASH"));
		assertEquals(-18, Arrays.binarySearch(WORDS_ARRAY, "HASH"));
		assertEquals(-19, sixBitBinarySearch(COMPRESSED_WORDS, "HUP"));
		assertEquals(-19, Arrays.binarySearch(WORDS_ARRAY, "HUP"));
		assertEquals(-33, sixBitBinarySearch(COMPRESSED_WORDS, "UMBRELL"));
		assertEquals(-33, Arrays.binarySearch(WORDS_ARRAY, "UMBRELL"));
		assertEquals(32, sixBitBinarySearch(COMPRESSED_WORDS, "UMBRELLA"));
		assertEquals(32, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLA"));
		assertEquals(-34, sixBitBinarySearch(COMPRESSED_WORDS, "UMBRELLAA"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLAA"));
		assertEquals(-34, sixBitBinarySearch(COMPRESSED_WORDS, "ZOP"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "ZOP"));
	}

	@Test
	public void specialCharactersSearch() {
		assertEquals(-1, sixBitBinarySearch(COMPRESSED_SPECIAL, ""));
		assertEquals(-1, Arrays.binarySearch(SPECIAL_ARRAY, ""));
		assertEquals(0, sixBitBinarySearch(COMPRESSED_SPECIAL, " "));
		assertEquals(0, Arrays.binarySearch(SPECIAL_ARRAY, " "));
		assertEquals(1, sixBitBinarySearch(COMPRESSED_SPECIAL, "@"));
		assertEquals(1, Arrays.binarySearch(SPECIAL_ARRAY, "@"));
		assertEquals(3, sixBitBinarySearch(COMPRESSED_SPECIAL, "}"));
		assertEquals(3, Arrays.binarySearch(SPECIAL_ARRAY, "}"));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Prefix search edge cases:

	private static final String[] CLIENT_DATA_ARRAY = {
		"ABCDEFGHIA CLIENT DATA CLIENT DATA CLIENT DATA",
		"ABCDEFGHIB CLIENT DATA CLIENT DATA CLIENT DATA",
		"ABCDEFGHIC CLIENT DATA CLIENT DATA CLIENT DATA",
		"ABCDEFGHID CLIENT DATA CLIENT DATA CLIENT DATA",
		"ABCDEFGHIE CLIENT DATA CLIENT DATA CLIENT DATA"};
	private static final byte[][] COMPRESSED_CLIENT_DATA = new byte[CLIENT_DATA_ARRAY.length][];

	static {
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_CLIENT_DATA, CLIENT_DATA_ARRAY);
	}

	@Test
	public void prefixSearchTest() {
		assertThrows(NullPointerException.class, () -> sixBitPrefixSearch(COMPRESSED_WORDS, NULL_REF));
		assertEquals(16, sixBitPrefixSearch(COMPRESSED_WORDS, "")); // Will match with the first entry it finds.
		assertEquals(3, sixBitPrefixSearch(COMPRESSED_WORDS, "A")); // Will get the first one it finds starting A.
		assertEquals(3, sixBitPrefixSearch(COMPRESSED_WORDS, "AB")); // Duplicate.
		assertEquals(3, sixBitPrefixSearch(COMPRESSED_WORDS, "ABA")); // Duplicate.
		assertEquals(4, sixBitPrefixSearch(COMPRESSED_WORDS, "AMBIT"));
		assertEquals(4, sixBitPrefixSearch(COMPRESSED_WORDS, "AMBITI"));
		assertEquals(4, sixBitPrefixSearch(COMPRESSED_WORDS, "AMBITIO"));
		assertEquals(-6, sixBitPrefixSearch(COMPRESSED_WORDS, "AMBITIONN"));
		assertEquals(-6, Arrays.binarySearch(WORDS_ARRAY, "AMBITIONN"));
		assertEquals(7, sixBitPrefixSearch(COMPRESSED_WORDS, "BA"));
		assertEquals(32, sixBitPrefixSearch(COMPRESSED_WORDS, "UMBRE"));
		assertEquals(32, sixBitPrefixSearch(COMPRESSED_WORDS, "UMBRELLA"));
		assertEquals(-34, sixBitPrefixSearch(COMPRESSED_WORDS, "UMBRELLAA"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLAA"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "UMBRELLAA "));
		assertEquals(-1, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHI "));
		assertEquals(2, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHI")); // First one it finds.
		assertEquals(0, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIA"));
		assertEquals(0, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIA "));
		assertEquals(-1, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIA"));
		assertEquals(-1, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIA "));
		assertEquals(1, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIB"));
		assertEquals(1, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIB "));
		assertEquals(-2, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIB B"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIB B"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIB"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIB "));
		assertEquals(2, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIC"));
		assertEquals(2, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIC "));
		assertEquals(3, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHID"));
		assertEquals(3, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHID "));
		assertEquals(4, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIE"));
		assertEquals(4, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIE "));
		assertEquals(-6, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIEE"));
		assertEquals(-6, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIEE"));
		assertEquals(-6, sixBitPrefixSearch(COMPRESSED_CLIENT_DATA, "ABCDEFGHIEE "));
		assertEquals(-6, Arrays.binarySearch(CLIENT_DATA_ARRAY, "ABCDEFGHIEE "));
	}

}
