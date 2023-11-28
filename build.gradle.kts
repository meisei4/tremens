// TODO: really look at all the gradle build files (including the toml file
//  and properties) and trim out any unnecessary bloat. These gradle files
//  are a blend of the jetbrains multiplatform demo styles and
//  the KaMPKit multiplatform repo so there is likely unneeded stuff

plugins {
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.0.0")
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }

    afterEvaluate {
        tasks.named("check").configure {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}