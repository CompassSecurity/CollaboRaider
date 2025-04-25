import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
}

version = "2.0.1"

repositories {
    mavenCentral()
}

dependencies {
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