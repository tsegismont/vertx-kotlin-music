import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.61"
  id("io.vertx.vertx-plugin") version "1.0.1"
  id("com.google.cloud.tools.jib") version "1.8.0"
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-jdbc-client") {
    exclude("com.mchange")
  }

  implementation("org.postgresql:postgresql:42.2.8")
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("com.google.cloud.sql:postgres-socket-factory:1.0.15")

  implementation("ch.qos.logback:logback-classic:1.2.3")

  testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
  testImplementation("org.testcontainers:postgresql:1.11.3")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("io.vertx:vertx-web-client")
}

val launcher_class = "sample.Main"
val jvm_flags = listOf("-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory")

vertx {
  vertxVersion = "3.8.4"
  launcher = launcher_class
  mainVerticle = "sample.App"
  jvmArgs = jvm_flags
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
  useJUnitPlatform()
}

jib {
  container {
    mainClass = launcher_class
    ports = listOf("8080")
    jvmFlags = jvm_flags
  }
}
