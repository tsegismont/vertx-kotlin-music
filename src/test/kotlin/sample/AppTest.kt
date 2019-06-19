package sample

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
internal class AppTest {

  private lateinit var webClient: WebClient

  @BeforeEach
  fun setUp(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(App(), testContext.succeeding {
      webClient = WebClient.create(vertx, webClientOptionsOf(defaultPort = 8080))
      testContext.completeNow()
    })
  }

  @Test
  internal fun `service should list of tracks`(testContext: VertxTestContext) {
    webClient.get("/music.json")
      .`as`(BodyCodec.jsonObject())
      .expect(ResponsePredicate.SC_OK)
      .expect(ResponsePredicate.JSON)
      .send(testContext.succeeding {
        val body = it.body()
        testContext.verify {
          val tracks = body.getJsonArray("music")
          assertFalse(tracks.isEmpty)
          val track = tracks.getJsonObject(0)
          assertTrue(track.containsKey("source") && track.getString("source").endsWith(".mp3"))
          testContext.completeNow()
        }
      })
  }
}