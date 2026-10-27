import java.util.Base64

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Ensure debug.keystore and .env exist before any project build.gradle.kts is evaluated
val keystoreFile = File(rootDir, "debug.keystore")
val keystoreBase64File = File(rootDir, "debug.keystore.base64")
if (!keystoreFile.exists() && keystoreBase64File.exists()) {
    runCatching {
        val cleanBase64 = keystoreBase64File.readText().replace("\\s+".toRegex(), "")
        keystoreFile.writeBytes(Base64.getDecoder().decode(cleanBase64))
    }
}

val dotEnvFile = File(rootDir, ".env")
val dotEnvExampleFile = File(rootDir, ".env.example")
if (!dotEnvFile.exists()) {
    runCatching {
        if (dotEnvExampleFile.exists()) {
            dotEnvExampleFile.copyTo(dotEnvFile, overwrite = false)
        } else {
            dotEnvFile.writeText("# Optional local secrets\n")
        }
    }
}

rootProject.name = "HealthHub"
include(":app")
