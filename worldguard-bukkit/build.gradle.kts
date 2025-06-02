import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("buildlogic.platform")
}

dependencies {
    "api"(project(":worldguard-core"))
    "api"(libs.worldedit.bukkit) { isTransitive = false }
    "compileOnly"(libs.commandbook) { isTransitive = false }

    "compileOnly"(libs.jetbrains.annotations) {
        because("Resolving Spigot annotations")
    }
    "testCompileOnly"(libs.jetbrains.annotations) {
        because("Resolving Spigot annotations")
    }
    "compileOnly"(libs.paperApi) {
        exclude("org.slf4j", "slf4j-api")
        exclude("junit", "junit")
    }

    "implementation"(libs.paperLib)
    "implementation"(libs.bstats.bukkit)

    "implementation"(libs.minelib.scheduler.canceller)
    "implementation"(libs.minelib.scheduler.global)
    "implementation"(libs.minelib.scheduler.entity)
    "implementation"(libs.minelib.scheduler.location)
}

tasks.named<Copy>("processResources") {
    val internalVersion = project.ext["internalVersion"]
    inputs.property("internalVersion", internalVersion)
    filesMatching("plugin.yml") {
        expand("internalVersion" to internalVersion)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        include(dependency(":worldguard-core"))
        include(dependency("org.bstats:"))
        include(dependency("io.papermc:paperlib"))
        include(dependency("io.github.projectunified:minelib-scheduler-common"))
        include(dependency("io.github.projectunified:minelib-scheduler-canceller"))
        include(dependency("io.github.projectunified:minelib-scheduler-global"))
        include(dependency("io.github.projectunified:minelib-scheduler-entity"))
        include(dependency("io.github.projectunified:minelib-scheduler-location"))

        relocate("org.bstats", "com.sk89q.worldguard.bukkit.bstats")
        relocate("io.papermc.lib", "com.sk89q.worldguard.bukkit.paperlib")
        relocate("io.github.projectunified.minelib", "com.sk89q.worldguard.bukkit.minelib")
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

configure<PublishingExtension> {
    publications.named<MavenPublication>("maven") {
        from(components["java"])
    }
}
