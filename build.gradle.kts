plugins {
    id("java")
}

group = "codes.shiftmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:5.0.0.Alpha2")
}

tasks.test {
    useJUnitPlatform()
}