import java.util.Base64

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.google.services) apply false
}

// Automatically prepare debug.keystore and .env on fresh GitHub clones so
// :app:validateSigningDebug, :app:validateSigningDebugAndroidTest, and :app:compileDebugKotlin succeed out-of-the-box.
val debugKeystoreFile = rootProject.file("debug.keystore")
val debugKeystoreBase64File = rootProject.file("debug.keystore.base64")
if (!debugKeystoreFile.exists() && debugKeystoreBase64File.exists()) {
    runCatching {
        val base64Content = debugKeystoreBase64File.readText().replace("\\s+".toRegex(), "")
        debugKeystoreFile.writeBytes(Base64.getDecoder().decode(base64Content))
    }
}

val dotEnvFile = rootProject.file(".env")
val dotEnvExampleFile = rootProject.file(".env.example")
if (!dotEnvFile.exists()) {
    runCatching {
        if (dotEnvExampleFile.exists()) {
            dotEnvExampleFile.copyTo(dotEnvFile, overwrite = false)
        } else {
            dotEnvFile.writeText("# Optional local secrets\n")
        }
    }
}

