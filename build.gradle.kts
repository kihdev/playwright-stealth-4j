import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.vanniktech.maven.publish") version "0.31.0"
    id("signing")
}

group = "io.github.kihdev"
version = "1.0.0"

object Meta {
    const val NAME = "playwright-stealth-4j"
    const val DESC = "A Kotlin-based library to enhance Playwright's stealth capabilities for Java, Kotlin, and Groovy."
    const val LICENSE = "MIT"
    const val GITHUB_REPO = "kihdev/playwright-stealth-4j"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.50.0")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true,
    ))

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(group.toString(), Meta.NAME, version.toString())

    pom {
        name.set(Meta.NAME)
        description.set(Meta.DESC)
        inceptionYear.set("2025")
        url.set("https://github.com/${Meta.GITHUB_REPO}")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        organization {
            name = "Keep it https Dev"
            url = "https://fabriziofortino.github.io/"
        }
        developers {
            developer {
                id.set("fabriziofortino")
                name.set("Fabrizio Fortino")
                url.set("https://github.com/fabriziofortino/")
            }
        }
        scm {
            url.set("https://github.com/${Meta.GITHUB_REPO}")
            connection.set("scm:git:git://github.com/${Meta.GITHUB_REPO}.git")
            developerConnection.set("scm:git:ssh://git@github.com/${Meta.GITHUB_REPO}.git")
        }
    }
}
