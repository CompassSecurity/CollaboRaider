import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
}

group = "ch.csnc"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.2")
}

tasks.register<Task>("captureBuildTime") {
    doLast {
        val buildTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm:ss"))
        file("build-time.properties").writeText(
            "" +
                    "build.version=$version\n" +
                    "build.time=$buildTime"
        )
    }
}

tasks.jar {
    dependsOn(tasks["captureBuildTime"])
    from(file("build-time.properties"))
}

tasks.test {
    useJUnitPlatform()
}