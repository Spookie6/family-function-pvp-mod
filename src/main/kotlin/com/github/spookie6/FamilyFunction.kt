package com.github.spookie6

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

object FamilyFunction : ModInitializer {
    private val logger = LoggerFactory.getLogger("family-function")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")

        ServerPlayerEvents.JOIN.register { player -> player.sendMessage(Text.of("hi"), false) }
	}
}