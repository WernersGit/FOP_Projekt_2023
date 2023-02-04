@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.javafx)
}

javafx {
    version = "17.0.1"
    modules("javafx.controls", "javafx.fxml", "javafx.swing")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(libs.annotations)
    implementation(libs.flatlaf)
}
