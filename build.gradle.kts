@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    java
    application
    alias(libs.plugins.style)
    alias(libs.plugins.jagr.gradle)
    alias(libs.plugins.javafx)
}

version = file("version").readLines().first()

jagr {
    assignmentId.set("projekt")
    submissions {
        val main by creating {
            from(project(":"))
            from(project(":application"))
            from(project(":domain"))
            from(project(":infrastructure"))

            // ACHTUNG!
            // Kommentieren Sie die folgenden Zeilen ein und setzen Sie im folgenden Bereich Ihre
            // TU-ID (NICHT Ihre Matrikelnummer!), Ihren Nachnamen und Ihren Vornamen
            // in Anführungszeichen (z.B. "ab12cdef" für Ihre TU-ID) ein! Geben Sie dabei die Daten eines beliebigen
            // Gruppenmitgliedes an.

            studentId.set("jw18xexo")
            firstName.set("Jonas")
            lastName.set("Werner")
        }
    }
    graders {
        val graderPublic by creating {
            graderName.set("FOP-2223-Projekt-Public")
            rubricProviderName.set("projekt.Projekt_RubricProvider")
            configureDependencies {
                implementation(libs.algoutils.tutor)
                implementation(libs.mockito.inline)
            }
        }
    }
}

dependencies {
    implementation(libs.annotations)
    implementation(libs.algoutils.student)
    implementation(libs.flatlaf)
    testImplementation(libs.junit.core)
    testImplementation(project(":application"))
    testImplementation(project(":infrastructure"))
    testImplementation(project(":domain"))
    implementation(project(":application"))
    runtimeOnly(project(":infrastructure"))
    compileOnly(project(":infrastructure"))
}

application {
    mainClass.set("projekt.Main")
}

tasks {
    val runDir = File("build/run")
    withType<JavaExec> {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
    }
    test {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

subprojects {
    apply(plugin = "java")
}
