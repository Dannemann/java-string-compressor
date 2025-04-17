package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;

/**
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressorBenchmark {

	private static final AsciiCompressor COMPRESSOR = new FourBitAsciiCompressor(true);
	private static final int MAX_STRINGS = 4096; // Must be a power of 2 for bitwise module.
	private static final byte[][] INPUT_STRINGS = new byte[MAX_STRINGS][];
	private static final byte[][] COMPRESSED_STRINGS = new byte[MAX_STRINGS][];
	private static final Random RANDOM = new Random();

	private static int index;

	static {
		int charSetLen = DEFAULT_4BIT_CHARSET.length;

		for (int i = 0; i < MAX_STRINGS; i++) {
			byte[] randomStrBytes = new byte[RANDOM.nextInt(30, 50)];

			for (int j = 0, randomStrBytesLen = randomStrBytes.length; j < randomStrBytesLen; j++)
				randomStrBytes[j] = DEFAULT_4BIT_CHARSET[RANDOM.nextInt(charSetLen)];

			INPUT_STRINGS[i] = randomStrBytes;
			COMPRESSED_STRINGS[i] = COMPRESSOR.compress(randomStrBytes);
		}
	}

//	@Benchmark
//	public void baseline() {
//	}

	@Benchmark
	public byte[] compress() {
		return COMPRESSOR.compress(INPUT_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
	}

	@Benchmark
	public byte[] decompress() {
		return COMPRESSOR.decompress(COMPRESSED_STRINGS[index++ & 0x7FFFFFFF & MAX_STRINGS - 1]);
	}

	/**
	 * For debugging (see JMH in build.gradle.kts).
	 */
	public static void main(String[] args) throws RunnerException {
		new Runner(
			new OptionsBuilder()
				.include(FourBitAsciiCompressorBenchmark.class.getSimpleName())
				.forks(0)
				.build())
			.run();
	}

}
