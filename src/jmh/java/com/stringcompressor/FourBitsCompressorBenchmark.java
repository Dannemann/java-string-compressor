package com.stringcompressor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.US_ASCII;

@State(Scope.Thread)
public class FourBitsCompressorBenchmark {

	private AsciiCompressor compressor;
	private byte[] input;

	@Setup(Level.Trial)
	public void setup() {
		compressor = new FourBitsAsciiCompressor();
		compressor.setThrowException(true);

		byte[] supportedCharset = compressor.supportedCharset;
		System.out.println(" ### supportedCharset: \"" + new String(supportedCharset, US_ASCII) + "\"");
		int len = supportedCharset.length;
		byte[] inputStr = Arrays.copyOf(supportedCharset, len * 2);
		System.arraycopy(supportedCharset, 0, inputStr, len, len);
		System.out.println(" ### inputStr: \"" + new String(inputStr, US_ASCII) + "\"");
		input = inputStr;
	}

	@Benchmark
	public void compress(Blackhole bh) {
		byte[] r = compressor.compress(input);
		bh.consume(r);
	}

//	@Benchmark
//	public void compress2(Blackhole bh) {
//		compressor.setThrowException(true);
//		byte[] r = compressor.compress(input);
//		bh.consume(r);
//	}

}
