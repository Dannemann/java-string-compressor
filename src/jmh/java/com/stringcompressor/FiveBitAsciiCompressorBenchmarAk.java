package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;

import static com.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;

/**
 * @author Jean Dannemann Carone
 */
public class FiveBitAsciiCompressorBenchmarAk {

	private static final AsciiCompressor COMPRESSOR = new FiveBitAsciiCompressor(true);
	private static final int MAX_STRINGS = 4096; // Must be a power of 2 for bitwise module.
	private static final byte[][] SMALL_INPUT_STRINGS = new byte[MAX_STRINGS][];
	private static final byte[][] BIG_INPUT_STRINGS = new byte[MAX_STRINGS][];
	private static final byte[][] SMALL_COMPRESSED_STRINGS = new byte[MAX_STRINGS][];
	private static final byte[][] BIG_COMPRESSED_STRINGS = new byte[MAX_STRINGS][];
	private static final Random RANDOM = new Random();

	private static int index;

	static {
		int charSetLen = DEFAULT_5BIT_CHARSET.length;

		for (int i = 0; i < MAX_STRINGS; i++) {
			byte[] smallString = new byte[RANDOM.nextInt(100)];
			byte[] bigString = new byte[RANDOM.nextInt(50000)]; // 50k characters.

			for (int j = 0, smallStrBytesLen = smallString.length; j < smallStrBytesLen; j++)
				smallString[j] = DEFAULT_5BIT_CHARSET[RANDOM.nextInt(charSetLen)];
			for (int j = 0, bigStrBytesLen = bigString.length; j < bigStrBytesLen; j++)
				bigString[j] = DEFAULT_5BIT_CHARSET[RANDOM.nextInt(charSetLen)];

			SMALL_INPUT_STRINGS[i] = smallString;
			SMALL_COMPRESSED_STRINGS[i] = COMPRESSOR.compress(smallString);
			BIG_INPUT_STRINGS[i] = bigString;
			BIG_COMPRESSED_STRINGS[i] = COMPRESSOR.compress(bigString);
		}
	}

//	@Benchmark
//	public void baseline() {
//	}

//	@Benchmark
//	public byte[] compressSmallStrings() {
//		return COMPRESSOR.compress(SMALL_INPUT_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
//	}

//	@Benchmark
//	public byte[] decompressSmallStrings() {
//		return COMPRESSOR.decompress(SMALL_COMPRESSED_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
//	}

//	@Benchmark
//	public byte[] compressBigStrings() {
//		return COMPRESSOR.compress(BIG_INPUT_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
//	}

	@Benchmark
	public byte[] decompressBigStrings() {
		return COMPRESSOR.decompress(BIG_COMPRESSED_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
	}

	/**
	 * For debugging (see JMH in build.gradle.kts).
	 */
	public static void main(String[] args) throws RunnerException {
		new Runner(
			new OptionsBuilder()
				.include(FiveBitAsciiCompressorBenchmarAk.class.getSimpleName())
				.forks(0)
				.build())
			.run();
	}

}
