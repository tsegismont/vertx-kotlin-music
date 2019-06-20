package sample

import io.reactiverse.kotlin.pgclient.queryAwait
import io.reactiverse.pgclient.PgClient
import io.reactiverse.pgclient.PgPool
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

class App : CoroutineVerticle() {

  private lateinit var pgClient: PgPool

  override suspend fun start() {
    pgClient = PgClient.pool(vertx, pgPoolOptions(config))

    val router = Router.router(vertx)

    router.route().handler(LoggerHandler.create())
    router.get("/music.json").handler { routingContext -> launch { listTracks(routingContext) } }
    router.get().handler(StaticHandler.create())

    vertx.createHttpServer()
      .requestHandler(router)
      .listenAwait(8080)
  }

  private suspend fun listTracks(routingContext: RoutingContext) {
    val pgRowSet = pgClient.queryAwait("SELECT title,album,artist,genre,source,duration,image FROM tracks")

    val tracks = JsonArray()

    pgRowSet.forEach {
      val track = rowToJsonObject(it)
      tracks.add(track)
    }

    val result = jsonObjectOf("music" to tracks).encode()

    routingContext.response().putHeader("Content-Type", "application/json").end(result)
  }
}