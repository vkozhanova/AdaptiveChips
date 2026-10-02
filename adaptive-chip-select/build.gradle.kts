plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "io.github.vkozhanova.adaptivechipselect"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    explicitApi()
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.vkozhanova"
            artifactId = "adaptive-chip-select"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("adaptive-chip-select")
                description.set(
                    "Adaptive chip selector for Compose: synchronized multi-row scrolling, " +
                            "magnetic snap, pager indicator, and single/multiple selection modes."
                )
                url.set("https://github.com/vkozhanova/AdaptiveChips")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("vkozhanova")
                        name.set("Vera Kozhanova")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/vkozhanova/AdaptiveChips.git")
                    developerConnection.set("scm:git:ssh://github.com/vkozhanova/AdaptiveChips.git")
                    url.set("https://github.com/vkozhanova/AdaptiveChips")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/vkozhanova/AdaptiveChips")
            credentials {
                username = providers.gradleProperty("gcp_username").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gcp_token").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
}