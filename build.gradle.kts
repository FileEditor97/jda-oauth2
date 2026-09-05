plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
    // https://mvnrepository.com/artifact/com.gradleup.shadow/shadow-gradle-plugin
    id("com.gradleup.shadow") version "9.6.1"
    id("org.jetbrains.dokka") version "2.2.0"
}

group = "dev.fileeditor"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/net.dv8tion/JDA
    compileOnly("net.dv8tion:JDA:6.5.0")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    compileOnly("com.squareup.okhttp3:okhttp:5.5.0")
    // https://mvnrepository.com/artifact/org.json/json
    compileOnly("org.json:json:20260814")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

artifacts {
    archives(tasks.shadowJar)
}

tasks.shadowJar {
    archiveBaseName.set("jda-oauth2")
    archiveClassifier.set("")
    archiveVersion.set(version.toString())

    minimize()  // Removes unused classes

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/LICENSE*")
    exclude("META-INF/NOTICE*")
    exclude("META-INF/DEPENDENCIES")

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "FiLe group",
        )
    }

    finalizedBy("sourcesJar")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}
