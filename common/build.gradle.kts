import com.texelsaurus.Versions
import com.texelsaurus.Properties

plugins {
    id("java-conv")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

repositories {
    exclusiveContent { // Sable
        forRepository {
            maven {
                url = uri("https://maven.ryanhcode.dev/releases")
                name = "RyanHCode Maven"
            }
        }
        filter {
            includeGroup("dev.ryanhcode.sable")
            includeGroup("dev.ryanhcode.sable-companion")
        }
    }
}

minecraft {
    version(Versions.minecraft)
    accessWideners(file("src/main/resources/${Properties.modid}.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    api("dev.ryanhcode.sable-companion:sable-companion-common-1.21.1:1.6.0")
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}