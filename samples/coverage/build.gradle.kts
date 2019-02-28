plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }

    macosX64("macos") {
        binaries {
            executable(listOf(DEBUG)) {
                entryPoint = "coverage.main"
            }
        }
//        compilations["main"].extraOpts = mutableListOf()
        binaries.getExecutable("test", DEBUG).apply {
            freeCompilerArgs = mutableListOf(
                    "-Xtemporary-files-dir=.",
                    "-Xcoverage",
                    "-Xlibrary-to-cover=${compilations["main"].output.classesDirs.singleFile.absolutePath}"
            )
        }
    }
}

tasks.create("createCoverageReport") {
    dependsOn("macosTest")

    description = "Create coverage report"

    doLast {
        exec {
//            workingDir = File("$buildDir/gcov")
            commandLine("llvm-profdata", "merge", "default.profraw", "-o", "program.profdata")
        }
        exec {
//            workingDir = File("$buildDir/gcov")
            commandLine("llvm-cov", "show", "$buildDir/bin/macos/testDebugExecutable/test.kexe", "-instr-profile", "program.profdata")
        }
    }
}