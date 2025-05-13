# java-string-compressor

Ultra-fast, low-level string compression library. Up to 50% memory reduction.

- 4 bits -> 50% compression rate
- 5 bits -> 38% compression rate
- 6 bits -> 25% compression rate

Fast! Tiny milliseconds to compress a 10 MB string. Check out the benchmarks.

Well tested! See the test directory for usage examples and edge cases.

### 4‑bit compressor (`FourBitAsciiCompressor`)

Compression rate: 50%   
Maximum of 16 different chars. Default charset: `0-9`, `;`, `#`, `-`, `+`, `.`, `,`

```java
byte[] data = str.getBytes(US_ASCII); // Assume data is a 100 megabytes string.
byte[] c = new FourBitAsciiCompressor().compress(data); // c is 50 megabytes.
```

### 5‑bit compressor (`FiveBitAsciiCompressor`)

Compression rate: 38%   
Maximum of 32 different chars. Default charset: `A-Z`, space, `.`, `,`, `\`, `-`, `@`

```java
byte[] data = str.getBytes(US_ASCII); // Assume data is a 100 megabytes string.
byte[] c = new FiveBitAsciiCompressor().compress(data); // c is 62 megabytes.
```

### 6‑bit compressor (`SixBitAsciiCompressor`)

Compression rate: 25%   
Maximum of 64 different chars. Default charset supports `A-Z`, `0-9`, and many punctuation marks which are defined at
`SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET`.

```java
byte[] data = str.getBytes(US_ASCII); // Assume data is a 100 megabytes string.
byte[] c = new SixBitAsciiCompressor().compress(data); // c is 75 megabytes.
```

### Defining your custom character set

Compressors have a set of default characters supported for compression. These are defined in constants
```DEFAULT_4BIT_CHARSET```, ```DEFAULT_5BIT_CHARSET```, and ```DEFAULT_6BIT_CHARSET```. You can define your own
character set by using any constructor with the ```supportedCharset``` parameter.

### Catching invalid characters

It’s useful to validate the input and throw errors when invalid characters are found.
You can enable character validation by using any constructor with the ```throwException``` parameter.
Validation isn’t recommended for production because you will probably be adding dozens of gigabytes to the memory,
and you don't want a single invalid character to halt the whole processes.
It’s better to occasionally display an incorrect character than to abort the entire operation.

### Preserving the original input string

By default, the compressor overwrites the original input byte array to minimize memory usage.
Very useful when dealing with big strings, avoiding duplicating them.
You can enable input preservation by using any constructor with the ```preserveOriginal``` parameter.

## Downloads

Add it to your Maven project:
```xml
<dependency>
    <groupId>io.github.dannemann</groupId>
    <artifactId>java-string-compressor</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:
```java
implementation("io.github.dannemann:java-string-compressor:1.0.0")
```

Or download the lastest release from: https://github.com/Dannemann/java-string-compressor/releases




if you need logging , check ZeroLog, ChronicleLog and similar tools