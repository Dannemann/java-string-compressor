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
class ManagedBulkAsciiCompressorTest extends BaseTest {

	@Test
	void batchCompressByteArrayTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<byte[]> fullSource = new ArrayList<>();
			final byte[][] destiny = new byte[totalElements * 2][]; // Half empty.
			final FourBitAsciiCompressor compressor = new FourBitAsciiCompressor(true, true);
			final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
			for (int j = 0; j < numberOfBatches; j++) {
				final byte[][] batch = generateRandomByteArray(batchSize, 0, 100, DEFAULT_4BIT_CHARSET);
				managed.compressAll(batch); // Managed batch compress.
				fullSource.addAll(List.of(batch));
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destiny.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destiny[j]);
					assertArrayEquals(fullSource.get(j), compressor.decompress(destiny[j]));
				} else
					assertNull(destiny[j]);
		}
	}

	@Test
	void batchCompressStringArrayTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<String> fullSource = new ArrayList<>();
			final byte[][] destiny = new byte[totalElements * 2][]; // Half empty.
			final FiveBitAsciiCompressor compressor = new FiveBitAsciiCompressor(true, true);
			final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
			for (int j = 0; j < numberOfBatches; j++) {
				final String[] batch = generateRandomStringArray(batchSize, 0, 100, DEFAULT_5BIT_CHARSET);
				managed.compressAll(batch); // Managed batch compress.
				fullSource.addAll(List.of(batch));
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destiny.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destiny[j]);
					assertEquals(fullSource.get(j), new String(compressor.decompress(destiny[j]), US_ASCII));
				} else
					assertNull(destiny[j]);
		}
	}

	@Test
	void batchCompressStringListTest() {
		for (int i = 0; i < 500; i++) {
			final int batchSize = RANDOM.nextInt(500, 1000);
			final int numberOfBatches = RANDOM.nextInt(500, 1000);
			final int totalElements = batchSize * numberOfBatches;
			final List<String> fullSource = new ArrayList<>();
			final byte[][] destiny = new byte[totalElements * 2][]; // Half empty.
			final SixBitAsciiCompressor compressor = new SixBitAsciiCompressor(true, true);
			final ManagedBulkAsciiCompressor managed = new ManagedBulkAsciiCompressor(compressor, destiny);
			for (int j = 0; j < numberOfBatches; j++) {
				final List<String> batch = generateRandomStringList(batchSize, 0, 100, DEFAULT_6BIT_CHARSET);
				managed.compressAll(batch); // Managed batch compress.
				fullSource.addAll(batch);
			}
			assertEquals(totalElements, managed.getCurrentIndex());
			for (int j = 0, len = destiny.length; j < len; j++)
				if (j < totalElements) {
					assertNotNull(destiny[j]);
					assertEquals(fullSource.get(j), new String(compressor.decompress(destiny[j]), US_ASCII));
				} else
					assertNull(destiny[j]);
		}
	}

}
