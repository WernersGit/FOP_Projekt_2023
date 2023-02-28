import org.sourcegrade.jagr.gradle.task.submission.SubmissionBuildTask

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
    withType<SubmissionBuildTask> {
        doFirst {
            throw GradleException("Submissions not supported yet")
        }
    }
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
