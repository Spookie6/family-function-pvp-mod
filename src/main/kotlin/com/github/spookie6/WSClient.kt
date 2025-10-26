package com.github.spookie6

import com.github.spookie6.FamilyFunction.logger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text

/**
 * Websocket client to connect to the bridge chat
 */
object WSClient {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingIntervalMillis = 20_000
        }
    }

    /**
     * Ws message channel
     */
    private val outgoing = Channel<String>(
        capacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var connectionJob: Job? = null
    private var server: MinecraftServer? = null

    /**
     * Initializes the ws client
     * @param[MinecraftServer] the minecraft server object
     */
    fun start(server: MinecraftServer?) {
        this.server = server
        if (connectionJob == null || connectionJob?.isActive == false) {
            connectionJob = CoroutineScope(Dispatchers.IO).launch {
                connectLoop()
            }
        }
    }

    /**
     * Stops the ws connection
     */
    fun stop() {
        connectionJob?.cancel()
        client.close()
    }

    /**
     * Adds a message to the ws message channel
     * @param[String] message to send
     */
    fun send(msg: String) {
        outgoing.trySend(msg)
        logger.info("[WSClient] Sending message to server: $msg")
    }

    /**
     * Starts a loop to handle ws send and received
     */
    private suspend fun connectLoop() = coroutineScope {
        while (isActive) {
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = "127.0.0.1",
                    port = 443,
                    path = "/"
                ) {
                    FamilyFunction.logger.info("WSClient: Connected to websocket server!")
                    val sender = launch {
                        for (msg in WSClient.outgoing) {
                            send(msg)
                        }
                    }
                    val receiver = launch {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                // Dispatch to the main server thread safely
                                FamilyFunction.logger.info("WSClient: Received message from discord: $text")
                                server?.execute {
                                    broadcastMessage(text)
                                }
                            }
                        }
                    }
                    joinAll(sender, receiver)
                }
            } catch (e: Exception) {
                FamilyFunction.logger.warn("WSClient: Connection failed (${e.message}), retrying in 5s...")
                delay(5000)
            }
        }
    }

    /**
     * Broadcast a message to all players on the server thread
     * @param[String] broadcasts a message to all players online in the minecraft server
     */
    private fun broadcastMessage(message: String) {
        (server ?: return).submit {
            server!!.playerManager.broadcast(Text.literal(message), false)
        }
    }
}
