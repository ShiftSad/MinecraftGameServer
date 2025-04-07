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

    implementation("org.jetbrains:annotations:26.0.2")
    implementation("com.google.code.gson:gson:2.12.1")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
}