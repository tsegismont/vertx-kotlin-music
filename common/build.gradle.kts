import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.6"
}
