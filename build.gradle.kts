import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
    id("me.champeau.jmh") version "0.7.3"
}

group = "com.dannemann"
version = "1.2.0"
description = "Ultra-fast, zero-allocation string compression library. Up to 50% memory reduction."

repositories {
    mavenCentral()
}

dependencies {
    // Enables running JMH straight from a main method (fixes "/META-INF/BenchmarkList").
    // Useful for debugging (you must set forks to 0 in OptionsBuilder).
    // Also, check if the dependency version matches jmhVersion at jmh section below.
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.4")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.build {
    doLast {
        copy {
            from("$rootDir/publishing/java-string-compressor-$version.pom")
            into(layout.buildDirectory.dir("libs"))
        }
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

tasks.test {
    useJUnitPlatform()
}

jmh {
    jmhVersion = "1.37"
    includes = listOf(".Benchmark.")
    benchmarkMode = listOf("avgt")
    fork = 2
    warmup = "10s"
    warmupIterations = 3
    iterations = 3
    timeOnIteration = "10s"
    threads = 1
    timeUnit = "ms"
    resultFormat = "TEXT"
    resultsFile = file(
        "$projectDir/benchmarks/results-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt")
}
