import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Specify current version of the extension
version = "2.0.1"

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.7")
}

tasks.jar {
    // Add current time and version to file "build-time.properties"
    val buildTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())

    val output = "build.version=$version\n" +
                 "build.time=$buildTime\n" +
                 "build.gradle=${gradle.gradleVersion}\n" +
                 "build.jdk=${System.getProperty("java.version")}\n"

    file("build-time.properties").writeText(output)
    from(file("build-time.properties"))
}

tasks.test {
    useJUnitPlatform()
}