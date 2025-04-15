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

	private AsciiCompressor compressor;
	private byte[] input;

	@Setup(Level.Trial)
	public void setup() {
		compressor = new FourBitsAsciiCompressor();

		byte[] supportedCharset = compressor.supportedCharset;
		int len = supportedCharset.length;
		byte[] inputStr = Arrays.copyOf(supportedCharset, len * 2);
		System.arraycopy(supportedCharset, 0, inputStr, len, len);
		input = inputStr;
	}

	@Benchmark
	public void compress(Blackhole bh) {
		byte[] r = compressor.compress(input);
		bh.consume(r);
	}

}
