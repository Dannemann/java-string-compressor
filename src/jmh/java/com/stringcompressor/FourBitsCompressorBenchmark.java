package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;

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

}
