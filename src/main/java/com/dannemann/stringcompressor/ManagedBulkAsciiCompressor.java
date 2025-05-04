package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.BulkCompressionException;

import java.util.List;

/**
 * @author Jean Dannemann Carone
 */
public class ManagedBulkAsciiCompressor {

	private final BulkAsciiCompressor bulk;
	private final byte[][] destiny;
	private final int destinyLength;

	private int currentIndex;

	public ManagedBulkAsciiCompressor(AsciiCompressor compressor, byte[][] destiny) {
		this.bulk = new BulkAsciiCompressor(compressor);
		this.destiny = destiny;
		this.destinyLength = destiny.length;
	}

	public void compressAll(byte[][] source) {
		int willEndAt = currentIndex + source.length;
		validate(willEndAt);
		bulk.bulkCompress(source, destiny, currentIndex);
		currentIndex = willEndAt;
	}

	public void compressAll(String[] source) {
		int willEndAt = currentIndex + source.length;
		validate(willEndAt);
		bulk.bulkCompress(source, destiny, currentIndex);
		currentIndex = willEndAt;
	}

	public void compressAll(List<String> source) {
		int willEndAt = currentIndex + source.size();
		validate(willEndAt);
		bulk.bulkCompress(source, destiny, currentIndex);
		currentIndex = willEndAt;
	}

	private void validate(int willEndAt) {
		if (willEndAt >= destinyLength) // TODO: Also warn by percentages.
			throw new BulkCompressionException("Source array length exceeds destination array length.");
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

}
