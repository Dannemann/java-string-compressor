package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class FourBitsCompressorBenchmark {

	private Compressor compressor;
	private byte[] input;

	@Setup(Level.Trial)
	public void setup() {
		compressor = new FourBitsCompressor();

		byte[] supportedChars = compressor.getSupportedChars();
		int len = supportedChars.length;
		byte[] inputStr = Arrays.copyOf(supportedChars, len * 2);
		System.arraycopy(supportedChars, 0, inputStr, len, len);
		input = inputStr;
	}

	@Benchmark
	public void compress(Blackhole bh) {
		byte[] r = compressor.compress(input);
		bh.consume(r);
	}

	//	public static void main(String[] args) throws RunnerException {
//		Options opt = new OptionsBuilder()
//			.include(".Benchmark.")
//			.warmupTime(TimeValue.seconds(1))
//			.warmupIterations(5)
//			.measurementTime(TimeValue.seconds(1))
//			.measurementIterations(5)
//			.threads(1)
//			.forks(1)
//			.shouldFailOnError(true)
//			.shouldDoGC(true)
//			.jvmArgs("-server")
//			.build();
//
//		new Runner(opt).run();
//	}

}
