plugins {
    kotlin("jvm") version "1.9.10"
    application
}
group = "com.rpg"
version = "1.0.0"
repositories {
    mavenCentral()
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    
    // Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-gson:2.3.5")
    implementation("io.ktor:ktor-server-cors:2.3.5")
    implementation("io.ktor:ktor-server-call-logging:2.3.5")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}
application {
    mainClass.set("com.rpg.MainKt")
}
kotlin {
    jvmToolchain(11)
}
tasks.test {
    useJUnitPlatform()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.rpg.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
