package com.github.spookie6

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

object FamilyFunction : ModInitializer {
    private val logger = LoggerFactory.getLogger("family-function")
    private val modScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var server: MinecraftServer? = null

	override fun onInitialize() {
		logger.info("Loading FamilyFunction mod.")

        modScope.launch {
            try {
                WSClient.connect()
            } catch (e: Exception) {
                logger.error("Websocket client error: $e")
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register { srv ->
            server = srv
            logger.info("Server started, reference saved for broadcasting.")
        }

        ServerMessageEvents.CHAT_MESSAGE.register { message, sender, _ ->
            val content: String = message.content.string

            modScope.launch {
                try {
                    WSClient.send("${sender.name}: $content")
                    logger.info("[Send to ws] ${sender.name}: $content")
                } catch (e: Exception) {
                    logger.error("Failed to send message to websocket: $e")
                }
            }
        }
	}

    /**
     * Broadcast a message to all players on the server thread safely.
     */
    fun broadcastMessage(message: String) {
        val srv = server ?: return
        srv.submit {
            srv.playerManager.broadcast(Text.literal(message), false)
        }
    }
}