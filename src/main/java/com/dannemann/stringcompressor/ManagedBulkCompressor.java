package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.exception.BulkCompressionException;
import com.dannemann.stringcompressor.util.TriConsumer;

import java.util.List;

/**
 * @author Jean Dannemann Carone
 */
public class ManagedBulkCompressor {

	private final BulkCompressor bulk;
	private final int destinationLength;

	private int currentIndex;

	public ManagedBulkCompressor(AsciiCompressor compressor, byte[][] destination) {
		this.bulk = new BulkCompressor(compressor, destination);
		this.destinationLength = destination.length;
	}

	public void compressAndAddAll(byte[][] source, TriConsumer<Integer, byte[], byte[]> callback) {
		int willEndAt = currentIndex + source.length;
		validate(willEndAt);
		bulk.bulkCompress(source, currentIndex, callback);
		currentIndex = willEndAt;
	}

	public void compressAndAddAll(byte[][] source) {
		compressAndAddAll(source, null);
	}

	public void compressAndAddAll(String[] source, TriConsumer<Integer, String, byte[]> callback) {
		int willEndAt = currentIndex + source.length;
		validate(willEndAt);
		bulk.bulkCompress(source, currentIndex, callback);
		currentIndex = willEndAt;
	}

	public void compressAndAddAll(String[] source) {
		compressAndAddAll(source, null);
	}

	public void compressAndAddAll(List<String> source, TriConsumer<Integer, String, byte[]> callback) {
		int willEndAt = currentIndex + source.size();
		validate(willEndAt);
		bulk.bulkCompress(source, currentIndex, callback);
		currentIndex = willEndAt;
	}

	public void compressAndAddAll(List<String> source) {
		compressAndAddAll(source, null);
	}

	private void validate(int willEndAt) {
		if (willEndAt > destinationLength) // TODO: Also warn by percentages.
			throw new BulkCompressionException("Source array length exceeds destination array length.");
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	// Unsafe methods. Can cause confusion.

	static void compressAndAddAll(AsciiCompressor compressor, byte[][] destination, byte[][] source) {
		new ManagedBulkCompressor(compressor, destination).compressAndAddAll(source);
	}

	static void compressAndAddAll(AsciiCompressor compressor, byte[][] destination, String[] source) {
		new ManagedBulkCompressor(compressor, destination).compressAndAddAll(source);
	}

	static void compressAndAddAll(AsciiCompressor compressor, byte[][] destination, List<String> source) {
		new ManagedBulkCompressor(compressor, destination).compressAndAddAll(source);
	}

}
