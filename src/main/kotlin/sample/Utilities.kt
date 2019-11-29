package sample

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.Row

internal fun pgConnectOptions(config: JsonObject): PgConnectOptions = pgConnectOptionsOf(
  port = config.getInteger("postgresPort", 5432),
  user = "music",
  password = "music",
  database = "musicdb",
  host = if (System.getenv("CLOUD_SQL_INSTANCE") != null) "/cloudsql/" + System.getenv("CLOUD_SQL_INSTANCE") else null
)

internal fun rowToJsonObject(row: Row) = jsonObjectOf(
  "title" to row.getString("title"),
  "album" to row.getString("album"),
  "artist" to row.getString("artist"),
  "genre" to row.getString("genre"),
  "source" to row.getString("source"),
  "duration" to row.getInteger("duration"),
  "image" to row.getString("image"),
  "trackNumber" to 0,
  "totalTrackCount" to 0
)

internal fun RoutingContext.json(json: JsonObject) =
  this.response().putHeader("Content-Type", "application/json").end(json.encode())
