package com.dannemann.stringcompressor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static com.dannemann.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static com.dannemann.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Jean Dannemann Carone
 */
class BulkAsciiCompressorTest extends BaseTest {

	@Test
	void batchCompressByteArrayTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<byte[]> fullSource = new ArrayList<>();
			final byte[][] destination = new byte[totalElements * 2][]; // Half empty.
			final FourBitAsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
			final BulkAsciiCompressor bulk = new BulkAsciiCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final byte[][] batch = generateRandomByteArray(batchSize, 0, 100, DEFAULT_4BIT_CHARSET);
				bulk.bulkCompress(batch, j * batchSize, null); // Batch compress.
				fullSource.addAll(List.of(batch));
			}
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertArrayEquals(fullSource.get(j), compressor.decompress(destination[j]));
				} else
					assertNull(destination[j]);
		}
	}

	@Test
	void batchCompressStringArrayTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<String> fullSource = new ArrayList<>();
			final byte[][] destination = new byte[totalElements * 2][]; // Half empty.
			final FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
			final BulkAsciiCompressor bulk = new BulkAsciiCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final String[] batch = generateRandomStringArray(batchSize, 0, 100, DEFAULT_5BIT_CHARSET);
				bulk.bulkCompress(batch, j * batchSize, null); // Batch compress.
				fullSource.addAll(List.of(batch));
			}
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertEquals(fullSource.get(j), new String(compressor.decompress(destination[j]), US_ASCII));
				} else
					assertNull(destination[j]);
		}
	}

	@Test
	void batchCompressStringListTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<String> fullSource = new ArrayList<>();
			final byte[][] destination = new byte[totalElements * 2][]; // Half empty.
			final SixBitAsciiCompressor compressor = new SixBitAsciiCompressor(true, true);
			final BulkAsciiCompressor bulk = new BulkAsciiCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final List<String> batch = generateRandomStringList(batchSize, 0, 100, DEFAULT_6BIT_CHARSET);
				bulk.bulkCompress(batch, j * batchSize, null); // Batch compress.
				fullSource.addAll(batch);
			}
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertEquals(fullSource.get(j), new String(compressor.decompress(destination[j]), US_ASCII));
				} else
					assertNull(destination[j]);
		}
	}

}
