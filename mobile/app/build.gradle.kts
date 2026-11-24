plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mindcheck"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.mindcheck"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Step 3 (Lecture 6): Navigation Component - nav_graph.xml, NavHostFragment, NavController
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Step 5: a shared ViewModel keeps the answers between screens and when the phone rotates
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Step 8 (Lecture 7): SlidingPaneLayout for the list + detail Help screen (two panes on tablets)
    implementation(libs.androidx.slidingpanelayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

// >> GIT_ALL_IN_ONE:preBuildStartApi
tasks.named("preBuild") { dependsOn(rootProject.tasks.named("startApi")) }
// << GIT_ALL_IN_ONE:preBuildStartApi
