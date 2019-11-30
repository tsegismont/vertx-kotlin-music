package sample

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import javax.sql.DataSource

internal fun jdbcClientDatasource(config: JsonObject): DataSource {
  val hikariConfig = HikariConfig()

  val cloudSqlInstance = System.getenv("CLOUD_SQL_INSTANCE")
  val port = config.getInteger("postgresPort", 5432)

  hikariConfig.jdbcUrl = if (cloudSqlInstance == null) "jdbc:postgresql://localhost:${port}/musicdb" else "jdbc:postgresql:///musicdb"
  hikariConfig.username = "music"
  hikariConfig.password = "music"

  if (cloudSqlInstance != null) {
    hikariConfig.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory")
    hikariConfig.addDataSourceProperty("cloudSqlInstance", cloudSqlInstance)
  }

  return HikariDataSource(hikariConfig)
}

internal fun rowToJsonObject(row: JsonObject) = jsonObjectOf(
  "id" to row.getString("id"),
  "title" to row.getString("title"),
  "album" to row.getString("album"),
  "artist" to row.getString("artist"),
  "genre" to row.getString("genre"),
  "source" to row.getString("source"),
  "image" to row.getString("image"),
  "trackNumber" to row.getInteger("track_number"),
  "totalTrackCount" to row.getInteger("total_track_count"),
  "duration" to row.getInteger("duration")
)

internal fun RoutingContext.json(json: JsonObject) =
  this.response().putHeader("Content-Type", "application/json").end(json.encode())
