package com.dannemann.stringcompressor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dannemann.stringcompressor.AsciiCompressor.getString;
import static com.dannemann.stringcompressor.FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET;
import static com.dannemann.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static com.dannemann.stringcompressor.SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Jean Dannemann Carone
 */
class ManagedBulkCompressorTest extends BaseTest {

	@Test
	void compressByteArrayTest() {
		for (int i = 0; i < 3000; i++) {
			final int quantity = RANDOM.nextInt(10_000, 20_000);
			final byte[][] source = generateRandomByteArray(quantity, 0, 1000, DEFAULT_4BIT_CHARSET);
			final byte[][] destination = new byte[quantity][];
			final FourBitAsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			managed.compressAndAddAll(source);
			for (int j = 0; j < quantity; j++)
				assertArrayEquals(source[j], compressor.decompress(destination[j]));
		}
	}

	@Test
	void batchCompressByteArrayTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<byte[]> fullSource = new ArrayList<>();
			final byte[][] destination = new byte[totalElements * 2][]; // Half empty.
			final FourBitAsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final byte[][] batch = generateRandomByteArray(batchSize, 0, 100, DEFAULT_4BIT_CHARSET);
				managed.compressAndAddAll(batch); // Managed batch compress.
				fullSource.addAll(List.of(batch));
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertArrayEquals(fullSource.get(j), compressor.decompress(destination[j]));
				} else
					assertNull(destination[j]);
		}
	}

	@Test
	void compressStringArrayTest() {
		for (int i = 0; i < 3000; i++) {
			final int quantity = RANDOM.nextInt(10_000, 20_000);
			final String[] source = generateRandomStringArray(quantity, 0, 1000, DEFAULT_5BIT_CHARSET);
			final byte[][] destination = new byte[quantity][];
			final FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			managed.compressAndAddAll(source);
			for (int j = 0; j < quantity; j++)
				assertEquals(source[j], getString(compressor.decompress(destination[j])));
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
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final String[] batch = generateRandomStringArray(batchSize, 0, 100, DEFAULT_5BIT_CHARSET);
				managed.compressAndAddAll(batch); // Managed batch compress.
				fullSource.addAll(List.of(batch));
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertEquals(fullSource.get(j), getString(compressor.decompress(destination[j])));
				} else
					assertNull(destination[j]);
		}
	}

	@Test
	void compressStringListTest() {
		for (int i = 0; i < 3000; i++) {
			final int quantity = RANDOM.nextInt(10_000, 20_000);
			final List<String> source = generateRandomStringList(quantity, 0, 1000, DEFAULT_6BIT_CHARSET);
			final byte[][] destination = new byte[quantity][];
			final SixBitAsciiCompressor compressor = new SixBitAsciiCompressor(true, true);
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			managed.compressAndAddAll(source);
			for (int j = 0; j < quantity; j++)
				assertEquals(source.get(j), getString(compressor.decompress(destination[j])));
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
			final ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, destination);
			for (int j = 0; j < numberOfBatches; j++) {
				final List<String> batch = generateRandomStringList(batchSize, 0, 100, DEFAULT_6BIT_CHARSET);
				managed.compressAndAddAll(batch); // Managed batch compress.
				fullSource.addAll(batch);
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destination.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destination[j]);
					assertEquals(fullSource.get(j), getString(compressor.decompress(destination[j])));
				} else
					assertNull(destination[j]);
		}
	}

}
