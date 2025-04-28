package com.stringcompressor;

/**
 * @author Jean Dannemann Carone
 */
class BaseBenchmark {

	static byte[] generate10MbString(byte[] charset) {
		return BaseTest.generateRandomString(10 * 1024 * 1024, charset);
	}

}
