plugins {
    id("java-library")
    // https://mvnrepository.com/artifact/com.gradleup.shadow/shadow-gradle-plugin
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "dev.fileeditor"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/net.dv8tion/JDA
    implementation("net.dv8tion:JDA:5.3.0")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")

    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20250107")
}
