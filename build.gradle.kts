
plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

repositories {
    mavenCentral()
}

val javafxVersion = "21"

dependencies {
    implementation("org.openjfx:javafx-controls:$javafxVersion")
    implementation("org.openjfx:javafx-fxml:$javafxVersion")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("fungorium.gui.FungoriumApp")
}
