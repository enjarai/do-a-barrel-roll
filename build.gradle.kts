plugins {
    `maven-publish`
    id("dev.architectury.loom")
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.3.1"
}

// Variables
class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val mcVersion = stonecutter.current.project.substringBeforeLast('-')
val mcDep = property("mod.mc_dep").toString()
val isSnapshot = hasProperty("env.snapshot")

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

// Dependencies
repositories {
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    maven("https://maven.enjarai.dev/releases")
    maven("https://maven.enjarai.dev/mirrors")
    maven("https://jitpack.io")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.su5ed.dev/releases")
}

dependencies {
    fun modrinth(name: String, dep: Any?) = "maven.modrinth:$name:$dep"

    fun ifStable(str: String, action: (String) -> Unit = { modImplementation(it) }) {
        if (isSnapshot) modCompileOnly(str) else action(str)
    }

    minecraft("com.mojang:minecraft:${mcVersion}")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:${mcVersion}+build.${property("deps.yarn_build")}:v2")
        if (stonecutter.eval(mcVersion, "1.20.6"))
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.20.5+build.3")
        else if (stonecutter.eval(mcVersion, "1.21"))
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
    })
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    val mixinSquared = "com.github.bawnorton.mixinsquared:mixinsquared-%s:${property("deps.mixin_squared")}"
    implementation(annotationProcessor(mixinSquared.format("common"))!!)

    modCompileOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-$loader")

    if (isFabric) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        include(implementation(mixinSquared.format("fabric"))!!)
        ifStable("com.terraformersmc:modmenu:${property("deps.modmenu")}")

        modApi("nl.enjarai:cicada-lib:${property("deps.cicada")}")
        include(modImplementation("me.lucko:fabric-permissions-api:${property("deps.perm_api")}")!!)

        modRuntimeOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-$loader")
    } else {
        if (loader == "forge") {
            "forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
            compileOnly(annotationProcessor(mixinExtras.format("common"))!!)
            include(implementation(mixinExtras.format("forge"))!!)
        } else
            "neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
        include(implementation(mixinSquared.format(loader))!!)
        modImplementation("org.sinytra.forgified-fabric-api:forgified-fabric-api:${property("deps.fapi")}")
    }
    // Config

    // Compat
    for (it in property("deps.compat").toString().split(',')) {
        @Suppress("UselessCallOnNotNull")
        if (it.isNullOrBlank()) continue
        val (modid, version) = it.split('=')
        modCompileOnly(modrinth(modid, version))
    }
}

// Loom config
loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/do_a_barrel_roll.accesswidener"))

    if (loader == "forge") forge {
        convertAccessWideners.set(true)
        mixinConfigs(
            "${mod.id}.mixins.json"
        )
    } else if (loader == "neoforge") neoForge {

    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
        if (environment == "client") programArgs("--username=enjarai") // No, its me now :3
    }

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = false
//        defaultRefmapName = "do_a_barrel_roll.refmap.json"
    }
}

// Tasks
val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

val publishAll = tasks.register("publishAll") {
    group = "publishing"
    dependsOn("publish")
    dependsOn("publishMods")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

// Resources
tasks.processResources {
    inputs.property("version", mod.version)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to mod.version,
        "mc" to mcDep,
        "fml" to if (loader == "neoforge") "1" else "45",
    )

    filesMatching("fabric.mod.json") { expand(map) }
    filesMatching("META-INF/mods.toml") { expand(map) }
    filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

// Env configuration
stonecutter {
    val j21 = eval(mcVersion, ">=1.20.6")
    java {
        withSourcesJar()
        sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
        targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    }
}

// Publishing
publishMods {
    val modrinthToken = findProperty("enjaraiModrinthToken")
    val curseforgeToken = findProperty("enjaraiCurseforgeToken")
    val githubToken = findProperty("enjaraiGithubToken")
    dryRun = modrinthToken == null || curseforgeToken == null || githubToken == null

    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName =
        "${mod.version} for ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}"
    version = "${project.version}-$loader"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(loader)

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)

        if (isFabric) {
            requires("fabric-api", "cicada")
            embeds("cardinal-components-api")
        } else {
            requires("forgified-fabric-api")
        }
        optional("yacl")
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)

        if (isFabric) {
            requires("fabric-api", "cicada")
            embeds("cardinal-components-api")
        } else {
            requires("forgified-fabric-api")
        }
        optional("yacl")
    }

    github {
        repository = property("publish.github").toString()
        accessToken = githubToken.toString()

        commitish = property("publish.branch").toString()
        tagName = "${project.version}-$loader"
    }
}

publishing {
    repositories {
        maven("https://maven.enjarai.dev/releases") {
            name = "enjaraiMaven"
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = property("mod.group").toString()
            artifactId = "do-a-barrel-roll"
            version = "$version-$loader"

            from(components["java"])
        }
    }
}