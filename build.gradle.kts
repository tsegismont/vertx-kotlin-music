import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.31"
  id("io.vertx.vertx-plugin") version "0.6.0"
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.2.2")

  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-rx-java2")
  implementation("io.vertx:vertx-jdbc-client")
  implementation("io.vertx:vertx-web")

  implementation("com.h2database:h2:1.4.196")

  testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
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

tasks.wrapper {
  gradleVersion = "5.2"
}
