# java-string-compressor
Ultra-fast, zero-allocation string compression library. Up to 50% memory reduction.

Fast! Tiny milliseconds to compress a 10 MB string. Check out the benchmarks.<br/>
Thoroughly tested! See the test directory for usage examples and edge cases.
```java
String data = "Assume this is a 100 MB string...";
byte[] c;

// 4‑bit compressor -> 50% compression rate
// Max of 16 different chars.
// Default charset: `0-9`, `;`, `#`, `-`, `+`, `.`, `,`
c = new FourBitAsciiCompressor().compress(data); // c is 50 MB.

// 5‑bit compressor -> 38% compression rate
// Max of 32 different chars.
// Default charset: `A-Z`, space, `.`, `,`, `\`, `-`, `@`
c = new FiveBitAsciiCompressor().compress(data); // c is 62 MB.

// 6‑bit compressor -> 25% compression rate
// Max of 64 different chars.
// Default charset: `A-Z`, `0-9`, and many punctuation marks.
c = new SixBitAsciiCompressor().compress(data); // c is 75 MB.
```

## Downloads
```xml
<dependency>
    <groupId>io.github.dannemann</groupId>
    <artifactId>java-string-compressor</artifactId>
    <version>1.2.0</version>
</dependency>
```
```java
implementation("io.github.dannemann:java-string-compressor:1.2.0")
```
Or download the latest JAR from: https://github.com/Dannemann/java-string-compressor/releases

## Documentation
This library exists to quickly compress massive volumes of strings. 
It is very useful when you need large datasets allocated in memory for quick access or compacted for storage.
We achieve this by removing all unnecessary bits from each character. But how?

An ASCII character is represented by 8 bits: `00000000` to `11111111`. 
This gives us 128 different slots to represent characters.
However, sometimes we need only a small subset of those characters rather than the entire set.
For example, if your data only contains numbers (0-9) and a few punctuation marks, 16 different characters can be enough to 
represent them, and we only need 4 bits (`0000` to `1111`) to represent 16 characters.
If your data only contains letters (A-Z, like customer names), a set of 32 different characters is sufficient, which can be 
represented by 5 bits.
But if you need both letters and numbers, 6 bits are sufficient.
This way, we can remove those unnecessary bits and store only the ones we need. 
This is exactly what this library does.

It is an alternative to Huffman Coding, as it compresses small texts and lets you binary-search directly on the compressed data.

To compress a string, you can easily use either `FourBitAsciiCompressor`, `FiveBitAsciiCompressor`, or `SixBitAsciiCompressor`.

### Creating a compressor object
```java
var compressor = new SixBitAsciiCompressor();
```

#### Defining your custom character set
Each compressor has a set of default supported characters which are defined in fields 
`FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET`, `FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET`, and `SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET`.
If you need a custom character set, use constructors with parameter `supportedCharset`:
```java
// Follows ASCII character ordering.
byte[] myCustom4BitCharset = {'!', '"', '#', '$', '%', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@'};
var compressor = new FourBitAsciiCompressor(myCustom4BitCharset);
```
**Important:** The order in which you list characters in this array matters, as it defines the lexicographic
order the binary search will follow. It's good practice to define your custom charset in standard ASCII order, like the example above.

#### Catching invalid characters (useful for testing and debugging)
It’s useful to validate the input and throw errors when invalid characters are found.
You can enable character validation by using any constructor with `throwException` parameter.
Validations are not recommended for production because you will likely be allocating massive amounts of data, and 
you don't want a single invalid character to halt the entire process.
It’s better to occasionally display an incorrect character than to abort the entire operation.
```java
public FiveBitAsciiCompressor(boolean throwException)
```

#### Preserving source byte arrays (useful for testing and debugging)
Whenever possible, try to read bytes directly from your input source without creating `String` objects from them.
This will keep your entire compression process zero-allocation (like this library), which boosts performance and reduces memory usage.
However, when dealing directly with `byte[]` instead of `Strings`, you will notice that the compressor overwrites the original 
input byte array to minimize memory usage, making it unusable.
To avoid this behavior and compress a copy of the original, enable input preservation by using any constructor with `preserveOriginal` parameter.
```java
public SixBitAsciiCompressor(byte[] supportedCharset, boolean throwException, boolean preserveOriginal)
```

### Compressing and decompressing
Once the compressor is instantiated, the compress and decompress process is straightforward:
```java
   byte[] compressed = compressor.compress(input);
   byte[] decompressed = compressor.decompress(compressed);
   String string = new String(decompressed, StandardCharsets.ISO_8859_1);
// String string = AsciiCompressor.getString(decompressed); // Same as above. Recommended.
```
We recommend using `AsciiCompressor.getString(byte[])` because the method can be updated whenever a more efficient way to encode a `String` is found.

**In case you can't work directly with byte arrays and need `String` objects for compression:**
To extract ASCII bytes from a `String` in the most efficient way (for compression), use `AsciiCompressor.getBytes(String)`.
However, the overloaded version `compressor.compress(String)` already calls it automatically, so just call the overloaded version.

### Where to store the compressed data?
In its purest form, a `String` is just a byte array (`byte[]`), and a compressed `String` is no different. 
You can store it anywhere you would store a `byte[]`. If you are compressing millions of different entries, a very common 
approach is to store each compressed string ordered in memory using a `byte[][]` (for binary search) or a B+Tree if you 
need frequent insertions (coming in the next release). The frequency of reads and writes plus business requirements will 
determine the best storage medium and data structure to use.

If the data is ordered before compression and stored in memory in a `byte[][]` as mentioned above, you can use the full power of binary 
search directly on the compressed data through `FourBitBinarySearch`, `FiveBitBinarySearch`, and `SixBitBinarySearch`.

### Binary search
Executing a binary search on compressed data is as simple as:
```java
byte[][] compressedData = new byte[100000000][]; // Data for 100 million customers.
// ...
SixBitBinarySearch binary = new SixBitBinarySearch(compressedData, false); // false == exact-match search.
int index = binary.search("key");
```
It is important to note that `compressedData` does not need to be completely filled. It could have 70 million entries 
and the binary search would still work. This is because the array of compressed data typically has extra space to 
accommodate new entries (usually with some incremental ID implementation to avoid insertions in the middle, but always at 
the end of the array), so unused slots (nulls) are placed at the end.

A more realistic approach is to organize your data with a unique prefix (usually an ID) and search for it. For example,
imagine each customer data entry in `compressedData` is organized like this:
```java
// ID # FullName # PhoneNumber # Address

"63821623849863628763#John Doe#(555) 555-1234#123 Main Street Anytown, CA 91234-5678"
```
We could find it like this:
```java
SixBitBinarySearch binary = new SixBitBinarySearch(compressedData, true); // true == prefix search.
int index = binary.search("63821623849863628763#");

if (index >= 0) {
    byte[] found = compressedData[index];
    String decompressed = compressor.decompress(found);
```
If you are using a custom character set to compress the data, you need to pass it to the binary search constructor:
```java
public FiveBitBinarySearch(byte[][] compressedData, boolean prefixSearch, byte[] charset)
```

### B+Tree
Coming in the next release.

### Other
Don't forget to check the JavaDocs for further information about each member.
Also check the test directory for additional examples.

### Logging
If you need logging, consider libraries like ZeroLog, ChronicleLog, Log4j 2 Async Loggers, and other similar tools
(we have not tested any of these). You will need a fast logging library, or it can become a bottleneck.

### Bulk / Batch compression
In some cases you may need to fetch your data in batches from a remote location or another third-party service.
java-string-compressor provides both `BulkCompressor` and `ManagedBulkCompressor` specifically for this task.
They help you automate the process of adding each batch to the correct position in the destination array where the
compressed data will be stored. Both currently support `byte[][]` as the destination for the compressed data. 

`BulkCompressor` is a "lower-level" utility where you must manage where each compressed string should be added in 
the target `byte[][]`. On the other hand, `ManagedBulkCompressor` encapsulates and automates this process, freeing you
from handling array positions and bounds. This is why we recommend `ManagedBulkCompressor` (which uses a `BulkCompressor` internally).

Both bulk compressors loop through the data in parallel by calling `IntStream.range().parallel()`.
```java
byte[][] compressedData = new byte[100000000][]; // Storage for a max of 100 million customers.
// ...
ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, compressedData);
// ...loop...
    managed.compressAndAddAll(batch); // batch is the list of strings/bytes to be compressed.
```
