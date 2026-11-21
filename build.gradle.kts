plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
}

// Convenience delegation tasks for multiplatform builds
tasks.register("packageDeb") {
    dependsOn(":composeApp:packageDeb")
}

subprojects {
    tasks.matching { it.name.contains("AarMetadata") }.configureEach {
        enabled = false
    }
}

