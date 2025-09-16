import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://repo1.maven.org/maven2/com/googlecode/soundlibs/")
    }
    mavenLocal()
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("javazoom:jlayer:1.0.1")
            implementation("com.mpatric:mp3agic:0.9.1")
        }
    }
}


compose.desktop {
    application {
        mainClass = "nl.rio282.simple_lrc_creator_visual.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "nl.rio282.simple_lrc_creator_visual"
            packageVersion = "1.0.0"
        }
    }
}
