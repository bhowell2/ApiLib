plugins {
  java
  `maven-publish`
  signing
}

repositories {
  mavenLocal()
  mavenCentral()
}

group = "io.github.bhowell2"
version = "0.1.1"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
//  testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

tasks.withType(JavaCompile::class) {
  options.compilerArgs.add("-Xlint:unchecked")
}

tasks {
  test {
    useJUnitPlatform()
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      artifactId = "api-lib"
      from(components["java"])
    }
  }
}
