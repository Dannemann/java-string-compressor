description = "A low-level extremely fast String compactor for Java."

plugins {
    `java-library`
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    // Uncomment this to run benchmarks with OptionsBuilder in a main method (fixes "/META-INF/BenchmarkList").
    // Debugging works by calling the main method, but you must set forks to 0.
    // Also, check if the version matches jmhVersion at jmh section below.
//    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
//    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.openjdk.jol:jol-core:0.17")
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "512m"
    maxHeapSize = "1g"
}

jmh {
    jmhVersion = "1.37"
    includes = listOf(".Benchmark.")
//    benchmarkMode = listOf("thrpt", "avgt")
    benchmarkMode = listOf("avgt")
    fork = 2
    warmup = "10s"
    warmupIterations = 3
    iterations = 3
    timeOnIteration = "10s"
    threads = 1
    timeUnit = "ns"
}
