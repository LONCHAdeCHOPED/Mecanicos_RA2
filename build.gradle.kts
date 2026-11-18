plugins {
    kotlin("jvm") version "2.4.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")

}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}