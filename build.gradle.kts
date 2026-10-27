import java.util.Base64

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.google.services) apply false
}

fun ensureDebugKeystoreAndEnv(rootDirFile: java.io.File) {
    val debugKeystoreFile = java.io.File(rootDirFile, "debug.keystore")
    val debugKeystoreBase64File = java.io.File(rootDirFile, "debug.keystore.base64")
    if (!debugKeystoreFile.exists() && debugKeystoreBase64File.exists()) {
        runCatching {
            val base64Content = debugKeystoreBase64File.readText().replace("\\s+".toRegex(), "")
            debugKeystoreFile.writeBytes(Base64.getDecoder().decode(base64Content))
        }
    }

    val dotEnvFile = java.io.File(rootDirFile, ".env")
    val dotEnvExampleFile = java.io.File(rootDirFile, ".env.example")
    if (!dotEnvFile.exists()) {
        runCatching {
            if (dotEnvExampleFile.exists()) {
                dotEnvExampleFile.copyTo(dotEnvFile, overwrite = false)
            } else {
                dotEnvFile.writeText("# Optional local secrets\n")
            }
        }
    }
}

ensureDebugKeystoreAndEnv(rootProject.projectDir)

subprojects {
    val rootDirFile = rootProject.projectDir
    tasks.matching {
        it.name == "preBuild" ||
            it.name.startsWith("validateSigning") ||
            it.name.contains("BuildConfig")
    }.configureEach {
        doFirst {
            ensureDebugKeystoreAndEnv(rootDirFile)
        }
    }
}
