plugins {
    application
    java
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("fungorium.Main")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fungorium.Main"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
