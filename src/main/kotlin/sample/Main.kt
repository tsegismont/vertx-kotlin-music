package sample

import io.vertx.core.Vertx
import io.vertx.kotlin.core.vertxOptionsOf

object Main {

  @JvmStatic
  fun main(args: Array<String>) {
    val vertx = Vertx.vertx(vertxOptionsOf(preferNativeTransport = true))
    vertx.deployVerticle(App())
  }
}
