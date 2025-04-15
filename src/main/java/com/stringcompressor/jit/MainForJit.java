package com.stringcompressor.jit;

import com.stringcompressor.Compressor;
import com.stringcompressor.FourBitsCompressor;

public class MainForJit {

	public static void main(String[] args) {
		Compressor compressor = new FourBitsCompressor();
		for (int i = 0; i < 1_000_000; i++) {
			compressor.compress("01234".getBytes());
		}
	}

}
