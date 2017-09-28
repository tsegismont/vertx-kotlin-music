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
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.sql.SQLConnection


/**
 * @author Thomas Segismont
 */
class MainVerticle : AbstractVerticle() {

  override fun start() {
    val config = JsonObject(
      "url" to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      "driver_class" to "org.h2.Driver"
    )

    val jdbcClient = JDBCClient.createShared(vertx, config)

    runScript(jdbcClient, "classpath:db.sql")
      .andThen(runScript(jdbcClient, "classpath:import.sql"))
      .subscribe({ println("Started!") }, { t -> println(t) })
  }

  private fun runScript(jdbcClient: JDBCClient, script: String): Completable {
    return getConnection(jdbcClient).flatMapCompletable { sqlConnection ->
      sqlConnection.rxExecute("RUNSCRIPT FROM '$script'")
    }
  }

  private fun getConnection(jdbcClient: JDBCClient): Single<SQLConnection> {
    return jdbcClient.rxGetConnection().flatMap { sqlConnection ->
      Single.just(sqlConnection).doAfterTerminate(sqlConnection::close)
    }
  }
}