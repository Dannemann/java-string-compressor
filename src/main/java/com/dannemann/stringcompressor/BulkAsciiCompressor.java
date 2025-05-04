package com.dannemann.stringcompressor;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Jean Dannemann Carone
 */
public record BulkAsciiCompressor(AsciiCompressor compressor) {

	public void bulkCompress(final byte[][] source, final byte[][] destiny, final int destinyStart) {
		IntStream.range(destinyStart, source.length + destinyStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinyStart;
			destiny[i] = compressor.compress(source[adjustedIndex]);
			if (!compressor.preserveOriginal)
				source[adjustedIndex] = null;
		});
	}

	public void bulkCompress(final String[] source, final byte[][] destiny, final int destinyStart) {
		IntStream.range(destinyStart, source.length + destinyStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinyStart;
			destiny[i] = compressor.compress(source[adjustedIndex]);
			if (!compressor.preserveOriginal)
				source[adjustedIndex] = null;
		});
	}

	public void bulkCompress(final List<String> source, final byte[][] destiny, final int destinyStart) {
		IntStream.range(destinyStart, source.size() + destinyStart).parallel().forEach(i -> {
			final int adjustedIndex = i - destinyStart;
			destiny[i] = compressor.compress(source.get(adjustedIndex));
			if (!compressor.preserveOriginal)
				source.set(adjustedIndex, null);
		});
	}

}
