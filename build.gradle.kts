plugins {
    application
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
