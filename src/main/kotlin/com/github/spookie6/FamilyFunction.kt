package com.github.spookie6

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents.ModifyCallback
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents.ModifyContext
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.component.ComponentMap
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

object FamilyFunction : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("family-function")
    var server: MinecraftServer? = null

	override fun onInitialize() {
		logger.info("Loading FamilyFunction mod.")

        ServerLifecycleEvents.SERVER_STARTED.register { srv ->
            server = srv
            logger.info("Server started, reference saved for broadcasting.")

            try {
                WSClient.start(server)
            } catch (e: Exception) {
                logger.error("Websocket client error: $e")
            }
        }

        ServerLifecycleEvents.SERVER_STOPPED.register { WSClient.stop() }

        ServerMessageEvents.CHAT_MESSAGE.register { message, sender, _ ->
            val name: String = sender.gameProfile.name
            val content: String = message.content.string
            try {
                WSClient.send("$name: $content")
            } catch (e: Exception) {
                logger.error("Failed to send message to websocket: $e")
            }
        }

        DefaultItemComponentEvents.MODIFY.register(ModifyCallback { context: ModifyContext? ->
            context!!.modify(
                Items.WIND_CHARGE,
                Consumer { builder: ComponentMap.Builder? -> builder?.add(DataComponentTypes.MAX_STACK_SIZE, 16) })
        })
        DefaultItemComponentEvents.MODIFY.register(ModifyCallback { context: ModifyContext? ->
            context!!.modify(
                Items.BREEZE_ROD,
                Consumer { builder: ComponentMap.Builder? -> builder?.add(DataComponentTypes.MAX_STACK_SIZE, 32) })
        })
        DefaultItemComponentEvents.MODIFY.register(ModifyCallback { context: ModifyContext? ->
            context!!.modify(
                Items.END_CRYSTAL,
                Consumer { builder: ComponentMap.Builder? -> builder?.add(DataComponentTypes.MAX_STACK_SIZE, 1) })
        })
	}
}