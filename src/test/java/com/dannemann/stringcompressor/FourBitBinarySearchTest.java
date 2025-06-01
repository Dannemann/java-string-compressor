package com.dannemann.stringcompressor;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.dannemann.stringcompressor.AsciiCompressor.getBytes;
import static com.dannemann.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
class FourBitBinarySearchTest extends BaseTest {

	// -----------------------------------------------------------------------------------------------------------------
	// Automated search tests:

	private static final AsciiCompressor COMPRESSOR = new FourBitAsciiCompressor(true, true);

	@Test
	void searchSmallStringsTest() {
		for (int length = 10; length <= 50; length++)
			for (int i = 0; i <= 30_000; i++) {
				final List<String> source = generateRandomUniqueOrderedStringList(500, length, length + 1, DEFAULT_4BIT_CHARSET);
				final byte[][] destination = new byte[700][];
				ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
				final FourBitBinarySearch bs = new FourBitBinarySearch(destination, false);
				for (int j = 0, len = source.size(); j < len; j++)
					assertEquals(j, bs.search(getBytes(source.get(j))));
			}
	}

	@RepeatedTest(100)
	void searchBigArrayTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(2_000_000, 0, 100, DEFAULT_4BIT_CHARSET);
		final byte[][] destination = new byte[4_000_000][];
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		final FourBitBinarySearch bs = new FourBitBinarySearch(destination, false);
		for (int i = 0, len = source.size(); i < len; i++)
			assertEquals(i, bs.search(getBytes(source.get(i))));
	}

	@RepeatedTest(100)
	void searchBigStringsTest() {
		final List<String> source = generateRandomUniqueOrderedStringList(50_000, 4500, 5000, DEFAULT_4BIT_CHARSET);
		final byte[][] destination = new byte[70_000][];
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, destination, source);
		final FourBitBinarySearch bs = new FourBitBinarySearch(destination, false);
		for (int i = 0, len = source.size(); i < len; i++)
			assertEquals(i, bs.search(getBytes(source.get(i))));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Search edge cases:

	private static final String[] EMPTY_WORD_ARRAY = {""};
	private static final String[] WORD_ARRAY = {"8765"};
	private static final String[] TWO_WORDS_ARRAY = {"11", "33"};
	private static final String[] WORDS_ARRAY = {
		"",                         // ""
		"01",                       // A
		"010201",                   // ABA
		"010201",                   // ABA
		"0113020920091514",         // AMBITION
		"0114050304152005",         // ANECDOTE
		"02",                       // B
		"020113021515",             // BAMBOO
		"030114251514",             // CANYON
		"0301181409220112",         // CARNIVAL
		"040114040512091514",       // DANDELION
		"04151216080914",           // DOLPHIN
		"0503120503200903",         // ECLECTIC
		"0512051608011420",         // ELEPHANT
		"0601021205",               // FABLE
		"070104070520",             // GADGET
		"070118040514",             // GARDEN
		"08151809261514",           // HORIZON
		"0825161415190919",         // HYPNOSIS
		"0901",                     // IA
		"09191520151605",           // ISOTOPE
		"102114071205",             // JUNGLE
		"110112050904151903151605", // KALEIDOSCOPE
		"12011420051814",           // LANTERN
		"1301180120081514",         // MARATHON
		"140502211201",             // NEBULA
		"1501190919",               // OASIS
		"16011801041524",           // PARADOX
		"172101182026",             // QUARTZ
		"1808011619150425",         // RHAPSODY
		"1901161608091805",         // SAPPHIRE
		"2001160519201825",         // TAPESTRY
		"2113021805121201"          // UMBRELLA
	};
	private static final String[] SPECIAL_ARRAY = {"#", ".", "0567", ";"};
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

	static int fourBitBinarySearch(byte[][] compressedData, String input) {
		return new FourBitBinarySearch(compressedData, false).search(getBytes(input));
	}

	static int fourBitPrefixSearch(byte[][] compressedData, String input) {
		return new FourBitBinarySearch(compressedData, true).search(getBytes(input));
	}

	@Test
	public void nullAndEmptySearchEdgeCaseTest() {
		assertThrows(NullPointerException.class, () -> fourBitBinarySearch(null, "0"));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(null, "0"));
		assertEquals(-1, fourBitBinarySearch(new byte[0][], NULL_REF));
		assertEquals(-1, Arrays.binarySearch(new String[0], null));
		assertEquals(-1, fourBitBinarySearch(new byte[0][], ""));
		assertEquals(-1, Arrays.binarySearch(new String[0], ""));
		assertEquals(-1, fourBitBinarySearch(new byte[0][], "0"));
		assertEquals(-1, Arrays.binarySearch(new String[0], "0"));
		assertThrows(NullPointerException.class, () -> fourBitBinarySearch(COMPRESSED_WORD, NULL_REF));
		assertThrows(NullPointerException.class, () -> Arrays.binarySearch(WORD_ARRAY, null));
		assertEquals(0, fourBitBinarySearch(COMPRESSED_EMPTY_WORD, ""));
		assertEquals(0, Arrays.binarySearch(EMPTY_WORD_ARRAY, ""));
		assertEquals(-1, fourBitBinarySearch(COMPRESSED_WORD, ""));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, ""));
		assertEquals(0, fourBitBinarySearch(COMPRESSED_WORDS, ""));
		assertEquals(0, Arrays.binarySearch(WORDS_ARRAY, ""));
	}

	@Test
	public void characterSearchEdgeCaseTest() {
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_EMPTY_WORD, "1"));
		assertEquals(-2, Arrays.binarySearch(EMPTY_WORD_ARRAY, "1"));
		assertEquals(-1, fourBitBinarySearch(COMPRESSED_WORD, "1"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "1"));
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_WORD, "9"));
		assertEquals(-2, Arrays.binarySearch(WORD_ARRAY, "9"));
		assertEquals(-1, fourBitBinarySearch(COMPRESSED_WORD, "1"));
		assertEquals(-1, Arrays.binarySearch(WORD_ARRAY, "1"));
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_TWO_WORDS, "2"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "2"));
		assertEquals(-1, fourBitBinarySearch(COMPRESSED_TWO_WORDS, "1"));
		assertEquals(-1, Arrays.binarySearch(TWO_WORDS_ARRAY, "1"));
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_WORDS, " "));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, " "));
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_WORDS, "-"));
		assertEquals(-2, Arrays.binarySearch(WORDS_ARRAY, "-"));
		assertEquals(1, fourBitBinarySearch(COMPRESSED_WORDS, "01"));
		assertEquals(1, Arrays.binarySearch(WORDS_ARRAY, "01"));
	}

	@Test
	public void wordSearchEdgeCaseTest() {
		assertEquals(0, fourBitBinarySearch(COMPRESSED_WORD, "8765"));
		assertEquals(0, Arrays.binarySearch(WORD_ARRAY, "8765"));
		assertEquals(-2, fourBitBinarySearch(COMPRESSED_TWO_WORDS, "112"));
		assertEquals(-2, Arrays.binarySearch(TWO_WORDS_ARRAY, "112"));
		assertEquals(-3, fourBitBinarySearch(COMPRESSED_WORDS, "0101")); // AA
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "0101")); // AA
		assertEquals(-3, fourBitBinarySearch(COMPRESSED_WORDS, "0102")); // AB
		assertEquals(-3, Arrays.binarySearch(WORDS_ARRAY, "0102")); // AB
		assertEquals(3, fourBitBinarySearch(COMPRESSED_WORDS, "010201")); // ABA
		assertEquals(3, Arrays.binarySearch(WORDS_ARRAY, "010201")); // ABA
		assertEquals(-18, fourBitBinarySearch(COMPRESSED_WORDS, "08011908")); // HASH
		assertEquals(-18, Arrays.binarySearch(WORDS_ARRAY, "08011908")); // HASH
		assertEquals(-19, fourBitBinarySearch(COMPRESSED_WORDS, "082116")); // HUP
		assertEquals(-19, Arrays.binarySearch(WORDS_ARRAY, "082116")); // HUP
		assertEquals(-33, fourBitBinarySearch(COMPRESSED_WORDS, "21130218051212")); // UMBRELL
		assertEquals(-33, Arrays.binarySearch(WORDS_ARRAY, "21130218051212")); // UMBRELL
		assertEquals(32, fourBitBinarySearch(COMPRESSED_WORDS, "2113021805121201")); // UMBRELLA
		assertEquals(32, Arrays.binarySearch(WORDS_ARRAY, "2113021805121201")); // UMBRELLA
		assertEquals(-34, fourBitBinarySearch(COMPRESSED_WORDS, "211302180512120101")); // UMBRELLAA
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "211302180512120101")); // UMBRELLAA
		assertEquals(-34, fourBitBinarySearch(COMPRESSED_WORDS, "261516")); // ZOP
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "261516")); // ZOP
	}

	@Test
	public void specialCharactersSearch() {
		assertEquals(-1, fourBitBinarySearch(COMPRESSED_SPECIAL, ""));
		assertEquals(-1, Arrays.binarySearch(SPECIAL_ARRAY, ""));
		assertEquals(0, fourBitBinarySearch(COMPRESSED_SPECIAL, "#"));
		assertEquals(0, Arrays.binarySearch(SPECIAL_ARRAY, "#"));
		assertEquals(1, fourBitBinarySearch(COMPRESSED_SPECIAL, "."));
		assertEquals(1, Arrays.binarySearch(SPECIAL_ARRAY, "."));
		assertEquals(3, fourBitBinarySearch(COMPRESSED_SPECIAL, ";"));
		assertEquals(3, Arrays.binarySearch(SPECIAL_ARRAY, ";"));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Prefix search edge cases:

	private static final String[] CLIENT_DATA_ARRAY = {
		"01020304050607080901#435384758934757894343",
		"01020304050607080902#435384758934757894343",
		"01020304050607080903#435384758934757894343",
		"01020304050607080904#435384758934757894343",
		"01020304050607080905#435384758934757894343"
	};

	private static final byte[][] COMPRESSED_CLIENT_DATA = new byte[CLIENT_DATA_ARRAY.length][];

	static {
		ManagedBulkCompressor.compressAndAddAll(COMPRESSOR, COMPRESSED_CLIENT_DATA, CLIENT_DATA_ARRAY);
	}

	@Test
	public void prefixSearchTest() {
		assertThrows(NullPointerException.class, () -> fourBitPrefixSearch(COMPRESSED_WORDS, NULL_REF));
		assertEquals(16, fourBitPrefixSearch(COMPRESSED_WORDS, "")); // Will match with the first entry it finds.
		assertEquals(3, fourBitPrefixSearch(COMPRESSED_WORDS, "01")); // Will get the first one it finds starting A.
		assertEquals(3, fourBitPrefixSearch(COMPRESSED_WORDS, "0102")); // Duplicate.
		assertEquals(3, fourBitPrefixSearch(COMPRESSED_WORDS, "010201")); // Duplicate.
		assertEquals(4, fourBitPrefixSearch(COMPRESSED_WORDS, "0113020920"));
		assertEquals(4, fourBitPrefixSearch(COMPRESSED_WORDS, "011302092009"));
		assertEquals(4, fourBitPrefixSearch(COMPRESSED_WORDS, "01130209200915"));
		assertEquals(-6, fourBitPrefixSearch(COMPRESSED_WORDS, "011302092009151414"));
		assertEquals(-6, Arrays.binarySearch(WORDS_ARRAY, "011302092009151414"));
		assertEquals(7, fourBitPrefixSearch(COMPRESSED_WORDS, "0201"));
		assertEquals(32, fourBitPrefixSearch(COMPRESSED_WORDS, "2113021805"));
		assertEquals(32, fourBitPrefixSearch(COMPRESSED_WORDS, "2113021805121201"));
		assertEquals(-34, fourBitPrefixSearch(COMPRESSED_WORDS, "211302180512120101"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "211302180512120101"));
		assertEquals(-34, Arrays.binarySearch(WORDS_ARRAY, "211302180512120101#"));
		assertEquals(-1, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "010203040506070809#"));
		assertEquals(2, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "010203040506070809")); // First one it finds.
		assertEquals(0, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080901"));
		assertEquals(0, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080901#"));
		assertEquals(-1, Arrays.binarySearch(CLIENT_DATA_ARRAY, "01020304050607080901"));
		assertEquals(-1, Arrays.binarySearch(CLIENT_DATA_ARRAY, "01020304050607080901#"));
		assertEquals(1, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080902"));
		assertEquals(1, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080902#"));
		assertEquals(-2, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080902#02"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "01020304050607080902#02"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "01020304050607080902"));
		assertEquals(-2, Arrays.binarySearch(CLIENT_DATA_ARRAY, "01020304050607080902#"));
		assertEquals(2, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080903"));
		assertEquals(2, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080903#"));
		assertEquals(3, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080904"));
		assertEquals(3, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080904#"));
		assertEquals(4, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080905"));
		assertEquals(4, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "01020304050607080905#"));
		assertEquals(-6, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "0102030405060708090505"));
		assertEquals(-6, Arrays.binarySearch(CLIENT_DATA_ARRAY, "0102030405060708090505"));
		assertEquals(-6, fourBitPrefixSearch(COMPRESSED_CLIENT_DATA, "0102030405060708090505#"));
		assertEquals(-6, Arrays.binarySearch(CLIENT_DATA_ARRAY, "0102030405060708090505#"));
	}

}
