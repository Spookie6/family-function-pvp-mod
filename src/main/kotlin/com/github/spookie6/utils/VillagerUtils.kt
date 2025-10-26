package com.github.spookie6.utils

import com.github.spookie6.FamilyFunction
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.math.random.Random
import net.minecraft.village.TradeOffer

object VillagerUtils {

    @JvmStatic
    fun shouldRemove(offer: TradeOffer): Boolean {
        val sellItem = offer.sellItem.item
        val id = Registries.ITEM.getId(sellItem).toString()
        if (id.contains("diamond")) {
            return true
        }
        return false
    }

    @JvmName("enchantItem")
    fun enchantItem(item: Item): ItemStack {
        val stack: ItemStack = ItemStack(item)

        val enchantedStack = EnchantmentHelper.enchant(
            Random.create(),
            stack,
            30,
            FamilyFunction.server?.registryManager,
            java.util.Optional.empty()
        )

        return stack
    }
}