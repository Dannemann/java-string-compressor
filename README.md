# java-string-compressor
Ultra-fast, zero-allocation string compression library. Up to 50% memory reduction.

Fast! Tiny milliseconds to compress a 10 MB string. Check out the benchmarks.<br/>
Well tested! See the test directory for usage examples and edge cases.

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
    <version>1.0.0</version>
</dependency>
```
```java
implementation("io.github.dannemann:java-string-compressor:1.0.0")
```
Or download the lastest JAR from: https://github.com/Dannemann/java-string-compressor/releases

## Documentation
This library exits to quickly compress a massive volume of strings. 
Very useful if you need massive data allocated in memory for quick access or compacted for storage.
We achieve this by removing all unnecessary bits from a character. But how?

An ASCII character is represented by 8 bits: `00000000` to `11111111`. 
This gives us 128 different slots to represent characters. 
But a lot of times we do not need all those characters, only a small sub-set of them.
For example, if your data only has numbers (0-9) and a few punctuations, 16 different characters can be enough to 
represent them, and we only need 4 bits (`0000` to `1111`) to represent 16 characters.
But if your data only has letters (A-Z, like customer names), a set of 32 different characters is enough, which can be 
represented by 5 bits.
And if you need both, 6 bits are enough.
This way we can remove those unnecessary bits and store only the ones we need. 
And this is exactly was this library do. 

Another important feature is searching. This library not only supports compacting, but also binary searching on the 
compacted data itself without deflating it, which will be explained later.

To compress a string, you can easily use either `FourBitAsciiCompressor`, `FiveBitAsciiCompressor`, or `SixBitAsciiCompressor`.

### Creating a compressor object
```java
var compressor = new SixBitAsciiCompressor();
```

#### Defining your custom character set
Each compressor have a set of default supported characters which are defined in fields 
`FourBitAsciiCompressor.DEFAULT_4BIT_CHARSET`, `FiveBitAsciiCompressor.DEFAULT_5BIT_CHARSET`, and `SixBitAsciiCompressor.DEFAULT_6BIT_CHARSET`.
If you need a custom character set, use constructors with parameter `supportedCharset`:
```java
// Follows ASCII character ordering.
byte[] myCustom4BitCharset = {'!', '"', '#', '$', '%', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@'};
var compressor = new FourBitAsciiCompressor(myCustom4BitCharset);
```
**Important:** The order in which you list characters in this array matters, as it defines the lexicographic
order the binary search will follow. It's good practice to define your custom charset in standard ASCII order, like the example above.

#### Catching invalid characters (useful for testing an debugging)
It’s useful to validate the input and throw errors when invalid characters are found.
You can enable character validation by using any constructor with `throwException` parameter.
Validations aren't recommended for production because you will probably be allocating massive amounts of gigabytes, and 
you don't want a single invalid character to halt the whole processes.
It’s better to occasionally display an incorrect character than to abort the entire operation.
```java
public FiveBitAsciiCompressor(boolean throwException)
```

#### Preserving source byte arrays (useful for testing an debugging)
Whenever possible, try to read straight bytes from your input source without creating `String` objects from them.
This will keep your whole compressing process zero-allocation (like this library), which boosts performance and memory saving.
But, by dealing directly with `byte[]` instead of `Strings`, you will notice that the compressor overwrites the original 
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
We recommend using `AsciiCompressor.getString(byte[])` because the method can be updated whenever a most efficient way to encode a `String` is found.

**In case you can't work directly with byte arrays and need `String` objects for compression:**
To extract ASCII bytes from a `String` in the most efficient way (for compression), do `AsciiCompressor.getBytes(String)`.
But the overloaded version `compressor.compress(String)` already calls it automatically, so, just call the overloaded version.

### Where to store the compressed data?
In its purest form, a `String` is just a byte array (`byte[]`), and a compressed `String` couldn't be different. 
You can store it anywhere you would store a `byte[]`. If you are compressing millions of different entries, a very common 
approach is to store each compressed string ordered in memory using a `byte[][]` (for binary search) or a B+Tree if you 
need frequent insertions (coming in the next release). The frequency of reads and writes + business requirements will 
tell the best media and data structure to use.

If the data is ordered before compression and stored in-memory in a `byte[][]` as mentioned above, you can use the full power of the binary 
search directly in the compressed data through `FourBitBinarySearch`, `FiveBitBinarySearch`, and `SixBitBinarySearch`.

### Binary search
Executing a binary search in compressed data is simple as:
```java
byte[][] compressedData = new byte[100000000][]; // Data for 100 million customers.
// ...
SixBitBinarySearch binary = new SixBitBinarySearch(compressedData, false); // false == exact-match search.
int index = binary.search("key");
```
It is important to note that ```compressedData``` does not need to be completely filled. It could have 70 million entries 
and the binary search would still work. This is because the array of compressed data typically has extra space to 
accommodate new entries (usually with some incremental ID implementation to avoid adding in the middle, but always at 
the end of the array), so unused slots (nulls) are placed at the end.

A more realistic approach is to organize your data with a unique prefix (usually an ID) and search for it. For example,
imagine each customer data in ```compressedData``` is organized like this:
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
In case you used are using a custom character set to compress the data, you need to pass it through the binary search constructor:
```java
public FiveBitBinarySearch(byte[][] compressedData, boolean prefixSearch, byte[] charset)
```

### B+Tree

Coming in the next release.

### Bulk / Batch compression

In some rare cases you need to fetch your data in batches from a remote location or another third party actor.
java-string-compressor provides both, `BulkCompressor` and `ManagedBulkCompressor` specifically for this task.
They help you automatize the process of adding each batch to the correct position in the destination array where the
compressed data will be stored. Both currently supports `byte[][]` as destination for the compressed data. 

`BulkCompressor` is a "lower-level" utility where you should manage where each compacted string should be added in 
the target `byte[][]`. In the other hand, `ManagedBulkCompressor` encapsulates and automatizes this process, avoiding you
from handle array positions and bounds. This is why we recommend `ManagedBulkCompressor` (which uses a `BulkCompressor` internally).

Both bulk compressors loop through the data in parallel by calling `IntStream.range().parallel()`.

```java
byte[][] compressedData = new byte[100000000][]; // Storage for a max of 100 million customers.
// ...
ManagedBulkCompressor managed = new ManagedBulkCompressor(compressor, compressedData);
// ...loop...
    managed.compressAndAddAll(batch); // batch is the list of strings to be compressed.
```

### Logging
If you need logging, search for libraries like ZeroLog, ChronicleLog, Log4j 2 Async Loggers, and other similar tools
(we did not test any of those). You will need a fast log library, or it can become a bottleneck.

### Other
Do not forget to check the JavaDocs with further information about each member.
Also check the test directory for additional examples.
