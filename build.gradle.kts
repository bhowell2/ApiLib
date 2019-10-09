plugins {
  java
  `maven-publish`
  signing
}

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
//  testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

tasks {
  test {
    useJUnitPlatform()
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {

    }
  }
}
