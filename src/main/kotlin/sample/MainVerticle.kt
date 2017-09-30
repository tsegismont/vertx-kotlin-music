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

import io.reactivex.Completable
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.sql.SQLConnection
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.LoggerHandler
import io.vertx.reactivex.ext.web.handler.StaticHandler


/**
 * @author Thomas Segismont
 */
class MainVerticle : AbstractVerticle() {

  lateinit var jdbcClient: JDBCClient

  override fun start() {
    val config = json {
      obj(
        "url" to "jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "driver_class" to "org.h2.Driver"
      )
    }

    jdbcClient = JDBCClient.createShared(vertx, config)

    runScript("classpath:db.sql")
      .andThen(runScript("classpath:import.sql"))
      .andThen(setupHttpServer())
      .subscribe({ println("Started!") }, Throwable::printStackTrace)
  }

  private fun runScript(script: String): Completable =
    getConnection(jdbcClient).flatMapCompletable { sqlConnection ->
      sqlConnection.rxExecute("RUNSCRIPT FROM '$script'")
    }

  private fun setupHttpServer(): Single<HttpServer> {
    val router = Router.router(vertx)
    router.route().handler(LoggerHandler.create())
    router.get("/music.json").handler { routingContext -> listTracks(routingContext) }
    router.get().handler(StaticHandler.create())
    return vertx.createHttpServer()
      .requestHandler(router::accept)
      .rxListen(8080)
  }

  private fun listTracks(routingContext: RoutingContext) {
    getConnection(jdbcClient)
      .flatMap { it.rxQuery("SELECT title,album,artist,genre,source,duration,image FROM tracks") }
      .map { toMusicJson(it.rows) }
      .subscribe({
        routingContext.response().putHeader("Content-Type", "application/json").end(Buffer(it.toBuffer()))
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

  private fun getConnection(jdbcClient: JDBCClient): Single<SQLConnection> =
    jdbcClient.rxGetConnection().flatMap { sqlConnection ->
      Single.just(sqlConnection).doAfterTerminate(sqlConnection::close)
    }
}