package com.dannemann.stringcompressor;

import com.dannemann.stringcompressor.util.TriConsumer;

import java.util.List;
import java.util.stream.IntStream;

/**
 * <p>Utility for compressing batches of strings using a provided {@link AsciiCompressor} and storing the compressed
 * results in a pre-allocated destination array.</p>
 * <p>Note: This is a "lower-level" utility. See class {@link ManagedBulkCompressor} for a higher-level abstraction
 * of an automated batching process.</p>
 * <p>This class is designed for bulk operations where multiple strings (as {@code String[]}, {@code byte[][]}, or
 * {@code List<String>}) need to be compressed efficiently and stored at specified positions in the destination array.
 * Compression is performed in parallel for better performance.</p>
 * <p>The destination array must be large enough to accommodate the compressed data starting from the specified index
 * provided in each method call.</p>
 * @param compressor The {@link AsciiCompressor} used to compress each string.
 * @param destination The destination array where compressed data will be stored.
 * @author Jean Dannemann Carone
 * @see ManagedBulkCompressor
 */
public record BulkCompressor(AsciiCompressor compressor, byte[][] destination) {

	/**
	 * <p>Compress a batch of strings and add them to the destination array starting at the specified index. A callback
	 * function is executed after each compression iteration (useful for logging and debugging).</p>
	 * <p>The arguments passed to the callback function are the index, the original string, and its compressed form. Note
	 * that this is executed in parallel, so, don't expect a sequential index in the callback.</p>
	 * @param source The batch of strings to be compressed.
	 * @param destinationStart The index at which elements will start being added to the destination array.
	 * @param callback A function that is called after each item is compressed and added to the destination array.
	 * @author Jean Dannemann Carone
	 */
	public void bulkCompress(final byte[][] source, final int destinationStart, final TriConsumer<Integer, byte[], byte[]> callback) {
		IntStream.range(destinationStart, source.length + destinationStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinationStart;
			final byte[] string = source[adjustedIndex];
			final byte[] compressed = compressor.compress(string);
			destination[i] = compressed;
			if (callback != null)
				callback.accept(adjustedIndex, string, compressed);
			if (!compressor.preserveOriginal)
				source[adjustedIndex] = null;
		});
	}

	/**
	 * Overloaded version of {@link #bulkCompress(byte[][], int, TriConsumer)}.
	 * @author Jean Dannemann Carone
	 */
	public void bulkCompress(final String[] source, final int destinationStart, final TriConsumer<Integer, String, byte[]> callback) {
		IntStream.range(destinationStart, source.length + destinationStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinationStart;
			final String string = source[adjustedIndex];
			final byte[] compressed = compressor.compress(string);
			destination[i] = compressed;
			if (callback != null)
				callback.accept(adjustedIndex, string, compressed);
			if (!compressor.preserveOriginal)
				source[adjustedIndex] = null;
		});
	}

	/**
	 * Overloaded version of {@link #bulkCompress(byte[][], int, TriConsumer)}.
	 * @author Jean Dannemann Carone
	 */
	public void bulkCompress(final List<String> source, final int destinationStart, final TriConsumer<Integer, String, byte[]> callback) {
		IntStream.range(destinationStart, source.size() + destinationStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinationStart;
			final String string = source.get(adjustedIndex);
			final byte[] compressed = compressor.compress(string);
			destination[i] = compressed;
			if (callback != null)
				callback.accept(adjustedIndex, string, compressed);
			if (!compressor.preserveOriginal)
				source.set(adjustedIndex, null);
		});
	}

}
