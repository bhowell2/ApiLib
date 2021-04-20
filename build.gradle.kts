plugins {
    java
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

val projectName = "api-lib"
group = "io.github.bhowell2"
version = "0.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType(JavaCompile::class) {
    options.compilerArgs.add("-Xlint:unchecked")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = projectName
            from(components["java"])
        }
    }
}

signing {
    /*
    * If the environment variables are not provided, then signing information
    * should have been provided in the users root gradle.properties (not the
    * gradle.properties of this repo, of course).
    * */
    // set by the environmentVariables:
    // ORG_GRADLE_PROJECT_signingKey,
    // ORG_GRADLE_PROJECT_signingKeyPassword
    val signingKey: String? by project
    val signingKeyPassword: String? by project
    if (signingKey != null && signingKeyPassword != null) {
        useInMemoryPgpKeys(signingKey, signingKeyPassword)
    }
    sign(publishing.publications["mavenJava"])
}

nexusPublishing {
    repositories {
        // username and password set by env variables:
        // ORG_GRADLE_PROJECT_sonatypeUsername
        // ORG_GRADLE_PROJECT_sonatypePassword
        sonatype()
    }
}

tasks.named<Wrapper>("wrapper") {
    version = "6.8.3"
}
