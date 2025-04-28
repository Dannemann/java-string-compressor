# java-string-compressor

A low-level ultra-fast string compactor. Up to 50% reduction.

- 4 bits -> 50% compression rate
- 5 bits -> 38% compression rate
- 6 bits -> 25% compression rate

Fast! Tiny milliseconds to compress a 10 MB string. Check out the benchmarks.

See the test directory for usage examples and edge cases.

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
