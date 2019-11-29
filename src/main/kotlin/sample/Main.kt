package sample

import io.vertx.core.Vertx

object Main {

  @JvmStatic
  fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(App())
  }
}
