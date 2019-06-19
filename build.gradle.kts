import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.31"
  id("io.vertx.vertx-plugin") version "0.6.0"
}

repositories {
  jcenter()
}

dependencies {
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-rx-java2")
  implementation("io.vertx:vertx-jdbc-client")
  implementation("io.vertx:vertx-web")
  implementation("com.h2database:h2:1.4.196")
  implementation(kotlin("stdlib-jdk8"))
}

vertx {
  vertxVersion = "3.7.1"
  mainVerticle = "sample.MainVerticle"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.wrapper {
  gradleVersion = "5.2"
}
