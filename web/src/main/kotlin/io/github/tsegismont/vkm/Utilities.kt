package io.github.tsegismont.vkm

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
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

internal fun rowToTrack(row: JsonObject) = Track(
  id = row.getString("id"),
  title = row.getString("title"),
  album = row.getString("album"),
  artist = row.getString("artist"),
  genre = row.getString("genre"),
  source = row.getString("source"),
  image = row.getString("image"),
  trackNumber = row.getInteger("track_number"),
  totalTrackCount = row.getInteger("total_track_count"),
  duration = row.getInteger("duration")
)

internal fun RoutingContext.json(data: Any) =
  this.response().putHeader("Content-Type", "application/json").end(Json.encodeToBuffer(data))
