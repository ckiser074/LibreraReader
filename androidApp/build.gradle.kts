import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
}

val appVersionName = project.findProperty("appVersion")?.toString() ?: "1.0.0"

extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = "mobi.librera5.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "mobi.librera5"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = appVersionName
    }

    signingConfigs {
        if (project.findProperty("RELEASE_STORE_FILE")?.toString()?.isNotBlank() == true) {
            create("release") {
                storeFile = file(project.property("RELEASE_STORE_FILE")!!)
                storePassword = project.property("RELEASE_STORE_PASSWORD")!!.toString()
                keyAlias = project.property("RELEASE_KEY_ALIAS")!!.toString()
                keyPassword = project.property("RELEASE_KEY_PASSWORD")!!.toString()
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning != null) {
                signingConfig = releaseSigning
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
