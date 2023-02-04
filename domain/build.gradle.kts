plugins {
    `java-library`
}

dependencies {
    implementation(libs.annotations)
    api(libs.algoutils.student)
    testImplementation(libs.junit.core)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
