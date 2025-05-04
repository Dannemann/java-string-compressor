package com.dannemann.stringcompressor;

/**
 * @author Jean Dannemann Carone
 */
class BaseBenchmark {

	static byte[] generate10MbString(byte[] charset) {
		return BaseTest.generateRandomStringBytes(10 * 1024 * 1024, charset);
	}

}
