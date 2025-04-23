package com.stringcompressor;

import com.stringcompressor.exception.CharacterNotSupportedException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.stringcompressor.FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jean Dannemann Carone
 */
public class FourBitAsciiCompressorTest {

    @Test
    public void validCustomCharsetTest() {
        byte[] customSupportedCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        new FourBitAsciiCompressor(customSupportedCharset);
    }

    @Test
    public void excessCustomCharsetTest() {
        byte[] customSupportedCharset = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        CharacterNotSupportedException e = assertThrows(
                CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
        assertEquals("4-bit compressor supports a minimum of 1 and a maximum of 16 different characters. Currently 17.", e.getMessage());
    }

    @Test
    public void invalidCustomCharsetTest() {
        byte[] customSupportedCharset = new byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        CharacterNotSupportedException e = assertThrows(
                CharacterNotSupportedException.class, () -> new FourBitAsciiCompressor(customSupportedCharset));
        assertEquals("Invalid character found in the custom supported charset: '\uFFFF' (code point -1)", e.getMessage());
    }

    @Test
    public void notAsciiCharCompressTest() {
        AsciiCompressor compressor = new FourBitAsciiCompressor(true);
        CharacterNotSupportedException e = assertThrows(
                CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{(byte) 'Ç'}));
        assertEquals("Only ASCII characters are supported. Invalid 'ￇ' (code point -57) in \"�\"", e.getMessage());
    }

    @Test
    public void invalidCharCompressTest() {
        AsciiCompressor compressor = new FourBitAsciiCompressor(true);
        CharacterNotSupportedException e = assertThrows(
                CharacterNotSupportedException.class, () -> compressor.compress(new byte[]{'Z'}));
        assertEquals("Character 'Z' (code point 90) is not defined in the supported characters array. String: \"Z\"", e.getMessage());
    }

    @Test
    public void compressDecompressEdgeCasesTest() {
        doCompressDecompressTest("");
        doCompressDecompressTest(new String(new byte[]{DEFAULT_4BIT_CHARSET[0]}, US_ASCII));
        doCompressDecompressTest(new String(new byte[]{DEFAULT_4BIT_CHARSET[0], DEFAULT_4BIT_CHARSET[1]}, US_ASCII));
        doCompressDecompressTest(new String(new byte[]{DEFAULT_4BIT_CHARSET[0], DEFAULT_4BIT_CHARSET[1], DEFAULT_4BIT_CHARSET[2]}, US_ASCII));
        doCompressDecompressTest(new String(new byte[]{DEFAULT_4BIT_CHARSET[DEFAULT_4BIT_CHARSET.length - 1]}, US_ASCII));
        doCompressDecompressTest(new String(DEFAULT_4BIT_CHARSET, US_ASCII));
    }

    @Test
    public void compressDecompressTest() {
        for (int length = 0; length < 500; length++)
            for (int i = 0; i < 3000; i++)
                doCompressDecompressTest(createRandomString(length));
    }

    private static void doCompressDecompressTest(String str) {
        AsciiCompressor compressor = new FourBitAsciiCompressor(true);
        byte[] compressed = compressor.compress(str.getBytes(US_ASCII));
        assertEquals(str.length() / 2 + (str.length() % 2 != 0 ? 2 : 1), compressed.length);
        byte[] decompressed = compressor.decompress(compressed);
        assertEquals(str.length(), decompressed.length);
        assertEquals(str, new String(decompressed, US_ASCII));
    }

    @Test
    public void ignoreInvalidCharTest() {
        AsciiCompressor compressor = new FourBitAsciiCompressor();
        byte[] compressed = compressor.compress(new byte[]{'0', (byte) 'Ç', '2', '3'});
        byte[] decompressed = compressor.decompress(compressed);
        assertEquals(",,23", new String(decompressed, US_ASCII));
    }

    @Test
    public void ignoreInvalidCharTest2() {
        AsciiCompressor compressor = new FourBitAsciiCompressor();
        for (int i = 0; i < 128; i++) {
            byte[] input = new byte[]{'0', (byte) i, '2', '3'};
            byte[] compressed = compressor.compress(input);
            byte[] decompressed = compressor.decompress(compressed);
            assertEquals(input.length, decompressed.length);
        }
    }

    // Utils:

    private String createRandomString(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append((char) (DEFAULT_4BIT_CHARSET[rand.nextInt(DEFAULT_4BIT_CHARSET.length)]));
        return sb.toString();
    }

}
