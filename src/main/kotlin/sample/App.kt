package sample

import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.sql.queryAwait
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class App : CoroutineVerticle() {

  private lateinit var sqlClient: JDBCClient

  override suspend fun start() {
    sqlClient = JDBCClient.create(vertx, jdbcClientDatasource(config));

    val router = Router.router(vertx)

    router.route().handler(LoggerHandler.create())
    router.get("/music.json").handler { routingContext -> launch { listTracks(routingContext) } }
    router.get().handler(StaticHandler.create())

    vertx.createHttpServer()
      .requestHandler(router)
      .listenAwait(8080)

    log.info("App is ready!")
  }

  private suspend fun listTracks(routingContext: RoutingContext) {
    val resultSet = sqlClient.queryAwait(query)

    val tracks = JsonArray()

    resultSet.rows.forEach {
      val track = rowToJsonObject(it)
      tracks.add(track)
    }

    val result = jsonObjectOf("music" to tracks)

    routingContext.json(result)
  }

  private companion object {
    const val query = "SELECT title,album,artist,genre,source,duration,image FROM tracks"
    val log = LoggerFactory.getLogger("app")
  }
}