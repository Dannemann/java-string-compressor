package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static com.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;

/**
 * @author Jean Dannemann Carone
 */
@State(Scope.Benchmark)
public class FiveBitAsciiCompressorBenchmark extends BaseBenchmark {

	AsciiCompressor compressor;
	byte[] toCompress;
	byte[] toDecompress;

	@Setup(Level.Trial)
	public void setUp() {
		compressor = new FiveBitAsciiCompressor();
		toCompress = generate10MbString(DEFAULT_5BIT_CHARSET);
		toDecompress = compressor.compress(toCompress);
	}

	@Benchmark
	public byte[] compress10Mb() {
		return compressor.compress(toCompress);
	}

	@Benchmark
	public byte[] decompress10Mb() {
		return compressor.decompress(toDecompress);
	}

	/**
	 * For debugging (see JMH in build.gradle.kts).
	 */
	public static void main(String[] args) throws RunnerException {
		new Runner(
			new OptionsBuilder()
				.include(FiveBitAsciiCompressorBenchmark.class.getSimpleName())
				.forks(0)
				.build())
			.run();
	}

}
