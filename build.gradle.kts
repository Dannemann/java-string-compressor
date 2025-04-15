description = "A low-level extremely fast String compactor for Java."

plugins {
    `java-library`
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjdk.jol:jol-core:0.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "512m"
    maxHeapSize = "1g"
}

jmh {
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
