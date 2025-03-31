plugins {
    id("java")
}

group = "codes.shiftmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.2.0.RC4")
}

tasks.test {
    useJUnitPlatform()
}