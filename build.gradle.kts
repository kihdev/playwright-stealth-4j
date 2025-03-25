plugins {
    kotlin("jvm") version "2.1.10"
    `maven-publish`
}

group = "io.github.kihdev.playwright-stealth-4j"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.50.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("playwright-stealth-4j")
                description.set("Playwright-Stealth for JVM – A Kotlin-based library to enhance Playwright's stealth capabilities for Java, Kotlin, and Groovy.")
                url.set("https://github.com/kihdev/playwright-stealth-4j")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/kihdev/playwright-stealth-4j.git")
                    developerConnection.set("scm:git:ssh://git@github.com:kihdev/playwright-stealth-4j.git")
                    url.set("https://github.com/kihdev/playwright-stealth-4j")
                }

                developers {
                    developer {
                        id.set("fabriziofortino")
                        name.set("Fabrizio Fortino")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kihdev/playwright-stealth-4j")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
    options.encoding = "UTF-8"
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

artifacts {
    archives(tasks.named("sourcesJar"))
    archives(tasks.named("javadocJar"))
}
