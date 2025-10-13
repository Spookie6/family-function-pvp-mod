package com.github.spookie6

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

object WSClient {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingIntervalMillis = 20_000
        }
    }

    private val outgoing = Channel<String>(Channel.UNLIMITED)

    suspend fun connect () {
        client.webSocket(
            method = HttpMethod.Get,
            host = "127.0.0.1",
            port = 8080,
            path = "/"
        ) {
            send("hello from server!")

            WSClient.outgoing.consumeEach { msg ->
                send(msg)
            }

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    FamilyFunction.broadcastMessage(text)
                }
            }
        }
        client.close()
    }

    fun send(msg: String) {
        outgoing.trySend(msg)
    }
}