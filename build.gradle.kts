import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.60"
  id("io.vertx.vertx-plugin") version "0.6.0"
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-web")
  implementation("io.reactiverse:reactive-pg-client:0.11.4")

  testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
  testImplementation("org.testcontainers:postgresql:1.11.3")
  testImplementation("org.slf4j:slf4j-jdk14:1.7.26")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("io.vertx:vertx-web-client")
}

vertx {
  vertxVersion = "3.7.1"
  mainVerticle = "sample.App"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
