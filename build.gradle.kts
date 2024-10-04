import java.io.ByteArrayOutputStream

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("idea")
    id("eclipse")
}

idea.module {
    isDownloadJavadoc = true
    isDownloadSources = true
}

// Not sure why you'd ever use eclipse for kotlin, but here you go.
eclipse.classpath {
    isDownloadJavadoc = true
    isDownloadSources = true
}

group = "net.glasslauncher"
version = "3.0.0"

application {
    mainClass.set("net.glasslauncher.repo.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
    implementation("io.ktor:ktor-server-pebble-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-http-redirect-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    // Why does this need to exist? Wtf?
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.apache.ant:ant:1.10.13")

    // Http client
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-apache5:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // Kotlin extensions
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")

    // Simplify my life when doing emails.
    implementation("org.apache.commons:commons-email:1.5")

    // JSON reading.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")

    // Database encryption.
    implementation("com.macasaet.fernet:fernet-java8:1.5.0")

    // Markdown parser and renderer. Very good, highly recommend.
    implementation("org.commonmark:commonmark:0.22.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.22.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.22.0")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.22.0")
    implementation("org.commonmark:commonmark-ext-heading-anchor:0.22.0")

    // Makes IntelliJ not scream about missing dirs inside templates
    implementation(fileTree("src/main/resources/net/glasslauncher/repo").matching {
        include("templates/**")
    })

//    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    implementation(kotlin("stdlib"))
}

tasks.withType(Tar::class.java).configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType(Zip::class.java).configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    val errout = ByteArrayOutputStream()

    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
        errorOutput = errout
        isIgnoreExitValue = true
    }

    if (stdout.toString().trim().length == 7) {
        return stdout.toString().trim()
    }
    else {
        error(errout)
    }
}

tasks.withType<ProcessResources> {
    var version = project.version.toString()
    val gitHash = getGitHash()
    if (gitHash.isNotEmpty()) {
        version = "${version}+${gitHash}"
    }

    inputs.property("version", version)

    filesMatching("net/glasslauncher/repo/version") {
        expand(mapOf("version" to version))
    }
}

tasks.run.configure {
    dependsOn(tasks.processResources)
}
