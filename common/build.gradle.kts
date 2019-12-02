import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  `maven-publish`
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.6"
}

publishing {
  publications {
    create<MavenPublication>("commonLibrary") {
      from(components["java"])
    }
  }
}