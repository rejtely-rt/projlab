
plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

javafx {
    version = "21"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("fungorium.HelloApp")
}
