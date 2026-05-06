import com.texelsaurus.Properties
import com.texelsaurus.Versions
import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("modloader-conv")
    id("net.neoforged.moddev") version ("0.1.74")
    id("com.modrinth.minotaur")
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

    exclusiveContent { // For aero, can't curse.maven for some reason
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

neoForge {
    version = Versions.neoForge
//  accessTransformers.add(file('src/main/resources/META-INF/accesstransformer.cfg'))
    runs {
        register("client") {
            client()
        }
        register("server") {
            server()
            programArgument("--nogui")
        }
    }

    mods {
        register(Properties.modid) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    // JEI
    //runtimeOnly("mezz.jei:jei-1.21.1-neoforge:19.14.1.144")
    // JADE
    implementation("curse.maven:jade-324717:7545219")
    implementation("curse.maven:the-one-probe-245211:7292875")

    implementation("curse.maven:architectury-api-419699:5786327")
    implementation("curse.maven:ftb-library-forge-404465:6807431")
    implementation("curse.maven:ftb-chunks-forge-314906:6504893")
    implementation("curse.maven:ftb-teams-forge-404468:6119437")
    // implementation("curse.maven:configured-457570:5873783")
    // implementation("curse.maven:carry-on-274259:5649344")
    // implementation("curse.maven:epic-fight-mod-405076:6816063")

    runtimeOnly("curse.maven:sable-1312371:8007005")
    runtimeOnly("maven.modrinth:create-aeronautics:YhZLrAFC")
    runtimeOnly("curse.maven:create-328085:7963363")
    runtimeOnly("curse.maven:lithium-360438:7740400")

    val sableCompanionDep = api("dev.ryanhcode.sable-companion:sable-companion-common-1.21.1:[1.6.0,)")
    jarJar(sableCompanionDep) {
        (sableCompanionDep as ExternalModuleDependency).version(Action {
            prefer("1.6.0")
        })
    }
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)

    disableVersionDetection()
    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: "debug_key"

    val mainFile = upload(Properties.curseProjectId, tasks.jar.get().archiveFile)
    mainFile.displayName = "${Properties.name}-${Versions.minecraft}-neoforge-$version"
    mainFile.changelogType = "markdown"
    mainFile.changelog = File(rootDir, "CHANGELOG.last.md").readText()
    mainFile.releaseType = Properties.distRelease
    Properties.distGameVersions.split(',').forEach { v -> mainFile.addGameVersion(v) }
    mainFile.addModLoader("NeoForge")
}

modrinth {
    token.set(System.getenv("MODRINTH_API_KEY") ?: "debug_key")
    projectId.set(Properties.modrinthProjectId)
    changelog.set(File(rootDir, "CHANGELOG.last.md").readText())
    versionName.set("${Properties.name}-${Versions.minecraft}-neoforge-$version")
    versionNumber.set("${Versions.minecraft}-${Versions.mod}")
    versionType.set(Properties.distRelease)
    gameVersions.set(Properties.distGameVersions.split(','))
    uploadFile.set(tasks.jar.get())
    loaders.add("neoforge")
}
tasks.modrinth.get().dependsOn(tasks.jar)
