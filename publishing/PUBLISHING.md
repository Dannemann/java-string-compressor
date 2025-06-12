Search for all instances of the old version (like `1.0.0`) in the project and change them.</br>
Also change `java-string-compressor-<VERSION>.pom` file name.
```
./gradlew clean build
```
The 4 needed artifacts will be placed at `build/libs`. Navigate there and execute:
```
gpg --armor --detach-sign --output java-string-compressor-<VERSION>.jar.asc java-string-compressor-<VERSION>.jar
gpg --armor --detach-sign --output java-string-compressor-<VERSION>-sources.jar.asc java-string-compressor-<VERSION>-sources.jar
gpg --armor --detach-sign --output java-string-compressor-<VERSION>-javadoc.jar.asc java-string-compressor-<VERSION>-javadoc.jar
gpg --armor --detach-sign --output java-string-compressor-<VERSION>.pom.asc java-string-compressor-<VERSION>.pom

for f in *.jar *.pom; do
    md5sum "$f"  | awk '{print $1}' > "$f.md5"
    sha1sum "$f" | awk '{print $1}' > "$f.sha1"
done

```
Copy all generated files to `io/github/dannemann/java-string-compressor/<VERSION>/` and compress it.</br>
Name it: `java-string-compressor-<VERSION>.zip`

Publish at: https://central.sonatype.com/publishing
