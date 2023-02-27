dependencies {
    implementation(libs.annotations)
    implementation(libs.algoutils.student)
    testImplementation(libs.junit.core)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
