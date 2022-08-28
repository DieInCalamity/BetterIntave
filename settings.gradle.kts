rootProject.name = "Intave-14"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

sourceControl {
    gitRepository(uri("https://github.com/intave/access.git")) {
        producesModule("de.jpx3.intave.access:intave-access")
    }
}
