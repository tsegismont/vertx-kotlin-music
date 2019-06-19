/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package sample

import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.sql.executeAwait
import io.vertx.kotlin.ext.sql.getConnectionAwait
import io.vertx.reactivex.ext.jdbc.JDBCClient as RxJDBCClient


/**
 * @author Thomas Segismont
 */
class App : CoroutineVerticle() {

  private lateinit var jdbcClient: JDBCClient
  private lateinit var rxJdbcClient: RxJDBCClient

  override suspend fun start() {
    val config = json {
      obj(
        "url" to "jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "driver_class" to "org.h2.Driver"
      )
    }

    jdbcClient = JDBCClient.createShared(vertx, config)
    rxJdbcClient = RxJDBCClient.newInstance(jdbcClient)

    runScript("classpath:db.sql")
    runScript("classpath:import.sql")
    setupHttpServer()
  }

  private suspend fun runScript(script: String) =
    jdbcClient.getConnectionAwait().executeAwait("RUNSCRIPT FROM '$script'")


  private suspend fun setupHttpServer() {
    val router = Router.router(vertx)
    router.route().handler(LoggerHandler.create())
    router.get("/music.json").handler { routingContext -> listTracks(routingContext) }
    router.get().handler(StaticHandler.create())
    vertx.createHttpServer()
      .requestHandler(router)
      .listenAwait(8080)
  }

  private fun listTracks(routingContext: RoutingContext) {
    rxJdbcClient.rxQuery("SELECT title,album,artist,genre,source,duration,image FROM tracks")
      .map(ResultSet::getRows)
      .map(this::toMusicJson)
      .map(JsonObject::encode)
      .subscribe({ json ->
        routingContext.response().putHeader("Content-Type", "application/json").end(json)
      }, routingContext::fail)
  }

  private fun toMusicJson(rows: List<JsonObject>): JsonObject = json {
    obj(
      "music" to json {
        array(
          rows.onEach {
            it.put("trackNumber", 0)
              .put("totalTrackCount", 0)
          }
        )
      })
  }
}