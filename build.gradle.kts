description = "A low-level extremely fast String compactor for Java."

plugins {
    `java-library`
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    // Enables running JMH straight from a main method (fixes "/META-INF/BenchmarkList").
    // Debugging is possible, but you must set forks to 0 in OptionsBuilder.
    // Also, check if the dependency version matches jmhVersion at jmh section below.
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

jmh {
    jmhVersion = "1.37"
    includes = listOf(".Benchmark.")
    benchmarkMode = listOf("thrpt", "avgt")
    fork = 2
    warmup = "10s"
    warmupIterations = 3
    iterations = 3
    timeOnIteration = "10s"
    threads = 1
    timeUnit = "ns"
}
