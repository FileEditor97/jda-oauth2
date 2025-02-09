plugins {
    id("java")
    // https://mvnrepository.com/artifact/com.gradleup.shadow/shadow-gradle-plugin
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "dev.fileeditor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}