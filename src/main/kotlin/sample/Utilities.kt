package sample

import io.reactiverse.kotlin.pgclient.pgPoolOptionsOf
import io.reactiverse.pgclient.PgPoolOptions
import io.reactiverse.pgclient.Row
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf

internal fun pgPoolOptions(config: JsonObject): PgPoolOptions = pgPoolOptionsOf(
  port = config.getInteger("postgresPort", 5432),
  user = "music",
  password = "music",
  database = "musicdb"
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