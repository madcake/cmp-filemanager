import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    ksp(libs.room.compiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Room
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.sqlite)

            //DI
            api(libs.koin.core)
            api(libs.koin.compose)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            // Voyager - viewmodels
            implementation(libs.voyager.screenmodel)

            // Datetime
            implementation(libs.datetime)

            // Image
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.zoomimage.zoomable)

            // Serialization
            implementation(libs.kotlinx.serialization)

            implementation("network.chaintech:compose-multiplatform-media-player:1.0.38")
            implementation(libs.hypnoticcanvas)
            implementation(libs.hypnoticcanvas.shaders)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.splitpanels)
            implementation(libs.ktor.desktop)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


compose.desktop {
    application {
        mainClass = "ru.madcake.filemanager.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.madcake.filemanager"
            packageVersion = "1.0.0"
        }
    }
}


room {
    schemaDirectory("$projectDir/schemas")
}
