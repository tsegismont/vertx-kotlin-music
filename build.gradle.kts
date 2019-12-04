plugins {
  kotlin("jvm") version "1.3.61" apply false
  id("io.vertx.vertx-plugin") version "1.0.1" apply false
  id("com.google.cloud.tools.jib") version "1.8.0" apply false
}

subprojects {
  repositories {
    mavenCentral()
  }
}
